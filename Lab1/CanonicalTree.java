import java.util.ArrayList;
import java.util.List;

public class CanonicalTree extends CodeTree{

    CanonicalTree(int[] arrOnWhichTreeWillBeBuild){
        List<Node> nodes = new ArrayList<Node>();
        for (int i = max(arrOnWhichTreeWillBeBuild); i >= 0; i--) {
            List<Node> newNodes = new ArrayList<Node>();
            if (i > 0) {
                for (int j = 0; j < arrOnWhichTreeWillBeBuild.length; j++) {
                    if (arrOnWhichTreeWillBeBuild[j] == i)
                        newNodes.add(new Leaf(j));
                }
            }

            // Merge pairs of nodes from the previous deeper layer
            for (int j = 0; j < nodes.size(); j += 2)
                newNodes.add(new InternalNode(nodes.get(j), nodes.get(j + 1)));
            nodes = newNodes;
        }
        root = (InternalNode)nodes.get(0);
        InitCodes(arrOnWhichTreeWillBeBuild.length);
    }

    private static int max(int[] array) {
        int result = array[0];
        for (int x : array)
            result = Math.max(x, result);
        return result;
    }
}
