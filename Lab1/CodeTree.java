
import java.util.*;

public final class CodeTree {

	public final InternalNode root;

	private List<List<Integer>> codes;
	

	public CodeTree(InternalNode root, int symbolLimit) {
		this.root = root;
		
		codes = new ArrayList<List<Integer>>();  // Initially all null
		for (int i = 0; i < symbolLimit; i++)
			codes.add(null);
		buildCodeList(root, new ArrayList<Integer>());  // Fill 'codes' with appropriate data
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
//			if (leaf.symbol >= codes.size())
//				throw new IllegalArgumentException("Symbol exceeds symbol limit");
//			if (codes.get(leaf.symbol) != null)
//				throw new IllegalArgumentException("Symbol has more than one code");
			codes.set(leaf.symbol, new ArrayList<Integer>(prefix));

		}
	}

	public List<Integer> getCode(int symbol) {
			return codes.get(symbol);
	}

	public List<List<Integer>> getCodes(){
		return codes;
	}

}
