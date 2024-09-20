package mazes.logic.carvers;

import graphs.EdgeWithData;
import graphs.minspantrees.MinimumSpanningTree;
import graphs.minspantrees.MinimumSpanningTreeFinder;
import mazes.entities.Room;
import mazes.entities.Wall;
import mazes.logic.MazeGraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;

/**
 * Carves out a maze based on Kruskal's algorithm.
 */
public class KruskalMazeCarver extends MazeCarver {
    MinimumSpanningTreeFinder<MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder;
    private final Random rand;

    public KruskalMazeCarver(MinimumSpanningTreeFinder
                                 <MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder) {
        this.minimumSpanningTreeFinder = minimumSpanningTreeFinder;
        this.rand = new Random();
    }

    public KruskalMazeCarver(MinimumSpanningTreeFinder
                                 <MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder,
                             long seed) {
        this.minimumSpanningTreeFinder = minimumSpanningTreeFinder;
        this.rand = new Random(seed);
    }

    @Override
    protected Set<Wall> chooseWallsToRemove(Set<Wall> walls) {
        // create an edge representing each wall
        Collection<EdgeWithData<Room, Wall>> edges = new ArrayList<>();
        for (Wall wall : walls) {
            double weight = rand.nextDouble();
            EdgeWithData<Room, Wall> edge = new EdgeWithData<>(wall.getRoom1(), wall.getRoom2(), weight, wall);
            edges.add(edge);
        }

        // create a new maze graph, find mst
        MazeGraph maze = new MazeGraph(edges);
        MinimumSpanningTree<Room, EdgeWithData<Room, Wall>> mst =
            this.minimumSpanningTreeFinder.findMinimumSpanningTree(maze);

        // add walls from MST
        Collection<EdgeWithData<Room, Wall>> mstEdges = mst.edges();
        Set<Wall> toRemove = new HashSet<>();

        for (EdgeWithData<Room, Wall> edge : mstEdges) {
            Wall wall = new Wall(edge.from(), edge.to(), edge.data().getDividingLine());
            toRemove.add(wall);
        }

        return toRemove;
    }
}
