package graphs.minspantrees;

import disjointsets.DisjointSets;
// import disjointsets.QuickFindDisjointSets;
import disjointsets.UnionBySizeCompressingDisjointSets;
import graphs.BaseEdge;
import graphs.KruskalGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Computes minimum spanning trees using Kruskal's algorithm.
 * @see MinimumSpanningTreeFinder for more documentation.
 */
public class KruskalMinimumSpanningTreeFinder<G extends KruskalGraph<V, E>, V, E extends BaseEdge<V, E>>
    implements MinimumSpanningTreeFinder<G, V, E> {

    protected DisjointSets<V> createDisjointSets() {
        // return new QuickFindDisjointSets<>();
        /*
        Disable the line above and enable the one below after you've finished implementing
        your `UnionBySizeCompressingDisjointSets`.
         */
        return new UnionBySizeCompressingDisjointSets<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    public MinimumSpanningTree<V, E> findMinimumSpanningTree(G graph) {
        // graph with no vertices
        if (graph.allVertices().isEmpty()) {
            return new MinimumSpanningTree.Success<>();
        }

        // sort edges in the graph in ascending weight order
        List<E> edges = new ArrayList<>(graph.allEdges());
        edges.sort(Comparator.comparingDouble(E::weight));

        DisjointSets<V> disjointSets = createDisjointSets();
        List<E> minimumSpanningTreeEdges = new ArrayList<>();

        for (V vertex : graph.allVertices()) {
            disjointSets.makeSet(vertex);
        }

        for (E edge : edges) {
            V vertexOne = edge.from();
            V vertexTwo = edge.to();

            // find sets of the vertices
            int oneSet = disjointSets.findSet(vertexOne);
            int twoSet = disjointSets.findSet(vertexTwo);

            // if vertices are not in the same set -> add edge to the MST
            if (oneSet != twoSet) {
                minimumSpanningTreeEdges.add(edge);
                disjointSets.union(vertexOne, vertexTwo);
            }
        }

        // does MST exist
        if (minimumSpanningTreeEdges.size() == graph.allVertices().size() - 1) {
            return new MinimumSpanningTree.Success<>(minimumSpanningTreeEdges);
        } else {
            return new MinimumSpanningTree.Failure<>();
        }
    }
}
