
import java.util.*;

public final class CanonicalCode {

	private int[] codeLengths;

	public CanonicalCode(int[] codeLens){
		codeLengths = codeLens.clone();
	}

	//Build canonical Huffman code
	public CanonicalCode(CodeTree tree, int symbolLimit) {
		codeLengths = new int[symbolLimit];
		buildCodeLengths(tree.root, 0);
	}
	

	private void buildCodeLengths(Node node, int depth) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			buildCodeLengths(internalNode.leftChild , depth + 1);
			buildCodeLengths(internalNode.rightChild, depth + 1);
		} else if (node instanceof Leaf) {
			int symbol = ((Leaf)node).symbol;
			codeLengths[symbol] = depth;
		}
	}

	public int getSymbolLimit() {
		return codeLengths.length;
	}

	public int getCodeLength(int symbol) {
		return codeLengths[symbol];
	}

	public int[] getCodeLengths() {
		return codeLengths;
	}
	
	// Returns the canonical code tree
	public CodeTree toCodeTree() {
		List<Node> nodes = new ArrayList<Node>();
		for (int i = max(codeLengths); i >= 0; i--) {
			List<Node> newNodes = new ArrayList<Node>();
			if (i > 0) {
				for (int j = 0; j < codeLengths.length; j++) {
					if (codeLengths[j] == i)
						newNodes.add(new Leaf(j));
				}
			}
			
			// Merge pairs of nodes from the previous deeper layer
			for (int j = 0; j < nodes.size(); j += 2)
				newNodes.add(new InternalNode(nodes.get(j), nodes.get(j + 1)));
			nodes = newNodes;
		}

		return new CodeTree((InternalNode)nodes.get(0), codeLengths.length);
	}


	private static int max(int[] array) {
		int result = array[0];
		for (int x : array)
			result = Math.max(x, result);
		return result;
	}
	
}
