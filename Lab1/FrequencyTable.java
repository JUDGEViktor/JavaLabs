
import java.util.*;


public final class FrequencyTable {

	private int[] frequencies;

	public FrequencyTable(int[] freqs) {
		frequencies = freqs.clone();
	}


	public int getSymbolLimit() {
		return frequencies.length;
	}
	

	public int get(int symbol) {
		return frequencies[symbol];
	}
	

	public void set(int symbol, int freq) {
		frequencies[symbol] = freq;
	}
	

	public void increment(int symbol) {
		frequencies[symbol]++;
	}


	public CodeTree buildCodeTree() {

		Queue<NodeWithFrequency> priorityQueue = new PriorityQueue<NodeWithFrequency>();

		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] > 0)
				priorityQueue.add(new NodeWithFrequency(new Leaf(i), i, frequencies[i]));
		}

		for (int i = 0; i < frequencies.length && priorityQueue.size() < 2; i++) {
			if (frequencies[i] == 0)
				priorityQueue.add(new NodeWithFrequency(new Leaf(i), i, 0));
		}
		
		// Tie together two nodes with the lowest frequency
		while (priorityQueue.size() > 1) {
			NodeWithFrequency x = priorityQueue.remove();
			NodeWithFrequency y = priorityQueue.remove();
			priorityQueue.add(new NodeWithFrequency(
				new InternalNode(x.node, y.node),
				Math.min(x.lowestSymbol, y.lowestSymbol),
				x.frequency + y.frequency));
		}

		return new CodeTree((InternalNode)priorityQueue.remove().node, frequencies.length);
	}
	

	private static class NodeWithFrequency implements Comparable<NodeWithFrequency> {
		
		public final Node node;
		public final int lowestSymbol;
		public final long frequency;
		
		
		public NodeWithFrequency(Node nd, int lowSym, long freq) {
			node = nd;
			lowestSymbol = lowSym;
			frequency = freq;
		}
		

		public int compareTo(NodeWithFrequency other) {
			if (frequency < other.frequency)
				return -1;
			else if (frequency > other.frequency)
				return 1;
			else if (lowestSymbol < other.lowestSymbol)
				return -1;
			else if (lowestSymbol > other.lowestSymbol)
				return 1;
			else
				return 0;
		}
		
	}
	
}
