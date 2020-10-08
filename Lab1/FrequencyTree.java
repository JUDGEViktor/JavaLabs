import java.util.PriorityQueue;
import java.util.Queue;

public class FrequencyTree extends CodeTree{

    FrequencyTree(int[] arrOnWhichTreeWillBeBuild){
        Queue<NodeWithFrequency> priorityQueue = new PriorityQueue<NodeWithFrequency>();

        for (int i = 0; i < arrOnWhichTreeWillBeBuild.length; i++) {
            if (arrOnWhichTreeWillBeBuild[i] > 0)
                priorityQueue.add(new NodeWithFrequency(new Leaf(i), i, arrOnWhichTreeWillBeBuild[i]));
        }

        for (int i = 0; i < arrOnWhichTreeWillBeBuild.length && priorityQueue.size() < 2; i++) {
            if (arrOnWhichTreeWillBeBuild[i] == 0)
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

        root = (InternalNode)priorityQueue.remove().node;
        InitCodes(arrOnWhichTreeWillBeBuild.length);
    }

    private class NodeWithFrequency implements Comparable<NodeWithFrequency> {

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
