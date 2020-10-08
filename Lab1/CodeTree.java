import java.util.*;

public class CodeTree {

	public InternalNode root;

	private List<List<Integer>> codes;

	CodeTree() {};

	public void InitCodes(int symbolLimit){
		codes = new ArrayList<List<Integer>>();  // Initially all null
		for (int i = 0; i < symbolLimit; i++)
			codes.add(null);
		buildCodeList(root, new ArrayList<Integer>());
		return;
	}

	private void buildCodeList(Node node, List<Integer> prefix) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			
			prefix.add(0);
			buildCodeList(internalNode.leftChild , prefix);
			prefix.remove(prefix.size() - 1);
			
			prefix.add(1);
			buildCodeList(internalNode.rightChild, prefix);
			prefix.remove(prefix.size() - 1);
			
		} else if (node instanceof Leaf) {
			Leaf leaf = (Leaf)node;
			codes.set(leaf.symbol, new ArrayList<Integer>(prefix));
		}
		return;
	}

	public List<Integer> getCode(int symbol) {
			return codes.get(symbol);
	}

	public List<List<Integer>> getCodes(){
		return codes;
	}

}
