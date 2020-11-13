package com.Viktor.main;

import java.util.*;

class CodeTree {

	public CodeTree() {};

	public void InitCodes(){
		codes = new HashMap<>();
		BuildCodeList(root, new ArrayList<Integer>());
		return;
	}

	private void BuildCodeList(Node node, List<Integer> prefix) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			
			prefix.add(0);
			BuildCodeList(internalNode.leftChild , prefix);
			prefix.remove(prefix.size() - 1);
			
			prefix.add(1);
			BuildCodeList(internalNode.rightChild, prefix);
			prefix.remove(prefix.size() - 1);
			
		} else if (node instanceof Leaf) {
			Leaf leaf = (Leaf)node;
			codes.put(leaf.symbol, new ArrayList<Integer>(prefix));
		}
		return;
	}


	public ArrayList<Integer> getCode(byte symbol) {
			return codes.get(symbol);
	}

	public InternalNode root;

	private HashMap<Byte, ArrayList<Integer>> codes;


}
