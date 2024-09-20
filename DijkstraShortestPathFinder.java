package graphs.shortestpaths;

import graphs.BaseEdge;
import graphs.Graph;
import priorityqueues.DoubleMapMinPQ;
import priorityqueues.ExtrinsicMinPQ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new DoubleMapMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
         */
        // return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        ExtrinsicMinPQ<V> pQueue = createMinPQ();
        Map<V, Double> distances = new HashMap<>();
        Map<V, E> spt = new HashMap<>();

        // null/0 outgoing edges case
        if (graph == null || graph.outgoingEdgesFrom(start).isEmpty()) {
            return spt;
        }

        // when start and end vertex are the same
        if (start == end) {
            return spt;
        }

        // add the start vertex to the priority queue
        pQueue.add(start, 0.0);
        distances.put(start, 0.0);

        while (!pQueue.isEmpty()) {
            V vertex = pQueue.removeMin();

            // when shortest path reached -> we're done
            if (vertex.equals(end)) {
                break;
            }

            for (E edge : graph.outgoingEdgesFrom(vertex)) {
                V target = edge.to();

                if (!distances.containsKey(target)) {
                    distances.put(target, Double.POSITIVE_INFINITY);
                }

                double oldDist = distances.get(target);
                double newDist = distances.get(vertex) + edge.weight();

                if (newDist < oldDist) {
                    distances.put(target, newDist);
                    spt.put(target, edge);

                    if (pQueue.contains(target)) {
                        pQueue.changePriority(target, newDist);
                    } else {
                        pQueue.add(target, newDist);
                    }
                }
            }
        }
        return spt;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        // if start and end vertex are the same -> only one vertex in spt
        if (start.equals(end)) {
            return new ShortestPath.SingleVertex<>(start);
        }

        E edge = spt.get(end);

        // null case
        if (edge == null) {
            return new ShortestPath.Failure<>();
        }

        // start from back of tree -> front
        List<E> path = new ArrayList<>();
        V lastVertex = end;
        path.add(edge);

        while (!lastVertex.equals(start)) {
            lastVertex = spt.get(lastVertex).from();
            if (lastVertex.equals(start)) {
                break;
            }
            path.add(spt.get(lastVertex));
        }
        // reverse, since it was going backwards
        Collections.reverse(path);
        return new ShortestPath.Success<>(path);
    }

}
