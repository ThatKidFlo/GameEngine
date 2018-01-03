package renderengine;

import models.ModelData;
import models.RawModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import utils.objparser.Vertex;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * An .obj file consists of 4 lists of information that completely describe a 3D model, namely:
 * <p>
 * - "v X Y Z" these lines will describe a single vertex, which is indexed in the order it appears in the model.
 * - "vt U V" these lines will describe texture coordinates for the model in the UV space.
 * - "vn X Y Z" these lines will describe the normal vectors of a single vertex.
 * <p>
 * The only problem is that not all data is in the correct order, meaning that v0 won't necessarily use vt0,
 * or vn0, etc.
 * <p>
 * - "f V1/T1/N1 V2/T2/N2 V3/T3/N3" these lines say that vertex number V1 uses the texture coordinate T1, and the
 * normal N1, and the same goes for the other two. The f stands for face, meaning that these three combinations of
 * data form a triangle in 3D space.
 * <p>
 *
 * Created by ThatKidFlo on 01.05.2016.
 */
public class OBJLoader {

    private static final String RESOURCES_FOLDER = "deps/";

    public static ModelData loadOBJ(String objFilename) {

        String line;
        List<Vertex> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(RESOURCES_FOLDER + objFilename + ".obj")))) {
            String[] currentLine = null;
            while (true) {
                line = reader.readLine();
                currentLine = line.split(" ");

                if (line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(
                            Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]),
                            Float.valueOf(currentLine[3])
                    );
                    vertices.add(new Vertex(vertices.size(), vertex));
                } else if (line.startsWith("vt ")) {
                    Vector2f texture = new Vector2f(
                            Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2])
                    );
                    textures.add(texture);
                } else if (line.startsWith("vn")) {
                    Vector3f normal = new Vector3f(
                            Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]),
                            Float.valueOf(currentLine[3])
                    );
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    break;
                }
            }
            //This point will be reached once arriving at the "f" part of the file.
            String[] vertex1, vertex2, vertex3;
            while (line != null && line.startsWith("f ")) {
                currentLine = line.split(" ");

                vertex1 = currentLine[1].split("/");
                vertex2 = currentLine[2].split("/");
                vertex3 = currentLine[3].split("/");

                Stream.of(vertex1, vertex2, vertex3)
                        .forEachOrdered((vert) -> processVertex(vert, vertices, indices));

                line = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to read the file!");
        }
        removeUnusedVertices(vertices);

        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray);
        int[] indicesArray = convertIndicesListToArray(indices);

        return new ModelData(verticesArray, texturesArray, normalsArray, indicesArray, furthest);
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];

        for(int i = 0; i < indicesArray.length; ++i) {
            indicesArray[i] = indices.get(i);
        }

        return indicesArray;
    }

    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals, float[] verticesArray, float[] texturesArray, float[] normalsArray) {
        float furthestPoint = 0.0f;
        for (int i = 0; i < vertices.size(); ++i) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f texture = textures.get(currentVertex.getTextureIndex());
            Vector3f normal = normals.get(currentVertex.getNormalIndex());

            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;

            texturesArray[i * 2] = texture.x;
            texturesArray[i * 2 + 1] = texture.y;

            normalsArray[i * 3] = normal.x;
            normalsArray[i * 3 + 1] = normal.y;
            normalsArray[i * 3 + 2] = normal.z;
        }
        return furthestPoint;
    }

    private static void removeUnusedVertices(List<Vertex> vertices) {
        vertices
                .stream()
                .filter((vertex) -> (!vertex.isSet()))
                .forEach((vertex) -> {
                    vertex.setTextureIndex(0);
                    vertex.setNormalIndex(0);
                });
    }

    private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {

        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;

        if (currentVertex.isSet()) {
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
        } else {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
        }
    }

    private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices, vertices);
            } else {
                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
                duplicateVertex.setNormalIndex(newNormalIndex);
                duplicateVertex.setTextureIndex(newTextureIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
            }
        }
    }

    /**
     * Loads an .obj file as a 3D object, using the loader provided as a second argument.
     *
     * @param filename - the name of the .obj model to load.
     * @param loader   - the loader to be used for loading the model.
     * @return - the newly created {@link RawModel}
     */
    @Deprecated
    public static RawModel loadObjModel(String filename, Loader loader) {
        FileReader fr = null;
        try {
            fr = new FileReader(new File(Loader.RESOURCES_FOLDER + filename + ".obj"));
        } catch (FileNotFoundException e) {
            System.err.println("Error loading .OBJ file!");
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(fr);

        String line;
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        int[] indicesArray = null;

        /*
          The point of this try is to ensure that the file is properly formatted (i.e. any X.parseX will fail fast,
          and be logged.
         */
        try {
            while (true) {
                // Fetch the next line in the .obj file.
                line = reader.readLine();
                // Split the file by spaces
                String[] currentLine = line.split(" ");
                if (line.startsWith("v ")) { // vertex coordinates
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if (line.startsWith("vt ")) { // texture coordinates
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) { // normal coordinates
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) { // line describes a face.
                    textureArray = new float[vertices.size() * 2];
                    normalsArray = new float[vertices.size() * 3];
                    // Upon entering this if, we can safely assume that we have passed to the last stage of the
                    // model, i.e. face description, hence we can break, and skip useless extra checks
                    break;
                }
            }

            // This while will handle all faces descriptions, and build the actual model from them.
            while (line != null) {

                // handle badly-formatted files, and comments
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }

                // Split into the 3 vertexes describing
                String[] currentLine = line.split(" ");

                String[] firstVertex = currentLine[1].split("/");
                String[] secondVertex = currentLine[2].split("/");
                String[] thirdVertex = currentLine[3].split("/");

                processVertex(firstVertex, indices, textures, normals, textureArray, normalsArray);
                processVertex(secondVertex, indices, textures, normals, textureArray, normalsArray);
                processVertex(thirdVertex, indices, textures, normals, textureArray, normalsArray);

                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        // Prepare raw arrays to be loaded in the VAO lists.
        for (Vector3f vertex : vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }
        return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
    }

    @Deprecated
    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
                                      List<Vector3f> normals, float[] textureArray, float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);

        // handle UV coords
        Vector2f currentTexture = textures.get(Integer.parseInt(vertexData[1]) - 1);
        textureArray[currentVertexPointer * 2] = currentTexture.x;
        // 1-y because OpenGL actually starts UV mapping from the top left corner
        textureArray[currentVertexPointer * 2 + 1] = 1 - currentTexture.y;

        Vector3f currentNormal = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNormal.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNormal.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNormal.z;
    }
}
