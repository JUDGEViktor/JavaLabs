
public class CanonicalCode {

	private int[] codeLengths;

	CanonicalCode() {};

	public CanonicalCode(int[] codeLens){
		codeLengths = codeLens.clone();
	}

	public void CountCodeLengths(CodeTree tree, int symbolLimit){
		codeLengths = new int[symbolLimit];
		CountCodeLengthsRecursively(tree.root, 0);
	}

	private void CountCodeLengthsRecursively(Node node, int depth) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			CountCodeLengthsRecursively(internalNode.leftChild , depth + 1);
			CountCodeLengthsRecursively(internalNode.rightChild, depth + 1);
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

	public int[] GetCodeLengths() {
		return codeLengths;
	}
	
}
