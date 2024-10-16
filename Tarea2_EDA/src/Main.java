import java.util.*;
import java.util.stream.Collectors;

class HuffmanNode {
    char character;
    int frequency;
    HuffmanNode left, right;

    HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    boolean isLeaf() {
        return left == null && right == null;
    }
}

class HuffmanTree {
    private HuffmanNode root;
    private Map<Character, String> huffmanCodes;

    public HuffmanTree(String text) {
        Map<Character, Integer> frequencyMap = buildFrequencyMap(text);
        root = buildHuffmanTree(frequencyMap);
        huffmanCodes = generateHuffmanCodes(root);
    }

    private Map<Character, Integer> buildFrequencyMap(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    private HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.frequency));

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();
            HuffmanNode parent = new HuffmanNode(left.frequency + right.frequency, left, right);
            priorityQueue.add(parent);
        }

        return priorityQueue.poll();
    }

    private Map<Character, String> generateHuffmanCodes(HuffmanNode node) {
        Map<Character, String> codes = new HashMap<>();
        generateHuffmanCodesRecursive(node, "", codes);
        return codes;
    }

    private void generateHuffmanCodesRecursive(HuffmanNode node, String code, Map<Character, String> codes) {
        if (node.isLeaf()) {
            codes.put(node.character, code);
        } else {
            generateHuffmanCodesRecursive(node.left, code + "0", codes);
            generateHuffmanCodesRecursive(node.right, code + "1", codes);
        }
    }

    public Map<Character, String> getHuffmanCodes() {
        return huffmanCodes;
    }

    public Map<Character, String> getCanonicalHuffmanCodes() {
        Map<Character, Integer> codeLengths = huffmanCodes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().length()));

        List<Map.Entry<Character, Integer>> sortedEntries = codeLengths.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());

        Map<Character, String> canonicalCodes = new HashMap<>();
        int currentCode = 0;
        int currentLength = sortedEntries.get(0).getValue();

        for (Map.Entry<Character, Integer> entry : sortedEntries) {
            char character = entry.getKey();
            int length = entry.getValue();

            if (length > currentLength) {
                currentCode <<= (length - currentLength);
                currentLength = length;
            }

            canonicalCodes.put(character, String.format("%" + length + "s", Integer.toBinaryString(currentCode)).replace(' ', '0'));
            currentCode++;
        }

        return canonicalCodes;
    }

    public String encodeTextWithCodes(Map<Character, String> codes, String text) {
        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(codes.get(c));
        }
        return encoded.toString();
    }
}

class HuffmanCanonical {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese el texto que desea comprimir:");
        String input = scanner.nextLine();

        // Crear el árbol de Huffman
        HuffmanTree huffmanTree = new HuffmanTree(input);

        // Mostrar los códigos de Huffman estándar
        System.out.println("\nCódigos de Huffman estándar:");
        Map<Character, String> huffmanCodes = huffmanTree.getHuffmanCodes();
        huffmanCodes.forEach((character, code) -> System.out.println(character + ": " + code));

        // Mostrar los códigos de Huffman canónico
        System.out.println("\nCódigos de Huffman canónico:");
        Map<Character, String> canonicalCodes = huffmanTree.getCanonicalHuffmanCodes();
        canonicalCodes.forEach((character, code) -> System.out.println(character + ": " + code));

        // Codificar el texto usando los códigos de Huffman canónico
        String encodedText = huffmanTree.encodeTextWithCodes(canonicalCodes, input);
        System.out.println("\nTexto codificado con Huffman canónico: " + encodedText);
        System.out.println("Este texto codificado es equivalente al texto ingresado: " + input);
    }
}