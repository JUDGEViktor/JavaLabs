package com.Viktor.main;

import java.util.*;

class FrequencyTree extends CodeTree {

    public FrequencyTree(HashMap<Byte, Integer> frequencies){
        Queue<NodeWithFrequency> priorityQueue = new PriorityQueue<NodeWithFrequency>();
        Iterator it = frequencies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int freq = (int)pair.getValue();
            byte byte_ = (byte)pair.getKey();
            priorityQueue.add(new NodeWithFrequency(new Leaf(byte_), byte_, freq));
        }
        //at least tree should has 2 nodes
        while (priorityQueue.size() < 2) {
            for(byte b = -128; b < 127; b++) {
                if (!frequencies.containsKey(b))
                    priorityQueue.add(new NodeWithFrequency(new Leaf(b), b, 0));
                if(priorityQueue.size() == 2)
                    break;
            }
        }

        // Tie together two nodes with the lowest frequency
        while (priorityQueue.size() > 1) {
            NodeWithFrequency x = priorityQueue.remove();
            NodeWithFrequency y = priorityQueue.remove();
            priorityQueue.add(new NodeWithFrequency(
                    new InternalNode(x.node, y.node),
                    Min(x.lowestSymbol, y.lowestSymbol),
                    x.frequency + y.frequency));
        }

        root = (InternalNode)priorityQueue.peek().node;
        InitCodes();
    }


    private byte Min(byte x1, byte x2){
        return x1 < x2 ? x1 : x2;
    }


    private static class NodeWithFrequency implements Comparable<NodeWithFrequency> {

        public final Node node;
        public final byte lowestSymbol;
        public final int frequency;

        public NodeWithFrequency(Node nd, byte lowSym, int freq) {
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

