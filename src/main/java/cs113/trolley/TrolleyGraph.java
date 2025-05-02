package cs113.trolley;

import javafx.scene.paint.Color;

import java.util.*;

// ********** Graph Construction ********** //
class TrolleyGraph {
    private List<TrolleyStation> stations;
    private List<TrolleyRoute> routes;

    public TrolleyGraph() {
        stations = new ArrayList<>();
        routes = new ArrayList<>();
    }

    // Add a new station (node) to the graph
    public void addStation(String name, int x, int y) {
        // todo: Implement this method to add a new station
        // Make sure to check if a station with the same name already exists
        if (getStationByName(name) == null) {
            stations.add(new TrolleyStation(name, x, y));
        }
    }

    // Get a station by its name
    public TrolleyStation getStationByName(String name) {
        // todo: Implement this method to find a station by name
        for (TrolleyStation station : stations) {
            if (station.getName().equals(name)) {
                return station;
            }
        }
        return null;
    }

    // Get all station names
    public Set<String> getStationNames() {
        Set<String> names = new HashSet<>();
        for (TrolleyStation station : stations) {
            names.add(station.getName());
        }
        return names;
    }

    // Add a new route (edge) between two stations
    public void addRoute(String fromStation, String toStation, int weight, Color color) {
        // todo: Implement this method to add a new route
        // Make sure both stations exist before adding the route
        TrolleyStation from = getStationByName(fromStation);
        TrolleyStation to = getStationByName(toStation);

        if (from == null || to == null) {
            return;
        }
        routes.add(new TrolleyRoute(fromStation, toStation, weight, color));
    }

    // Get all stations
    public List<TrolleyStation> getStations() {
        return stations;
    }

    // Get all routes
    public List<TrolleyRoute> getRoutes() {
        return routes;
    }

    // ********** Adjacency Lists ********** //
    public List<String> getAdjacentStations(String stationName) {
        // todo: Implement this method to find all stations connected to the given station
        List<String> adjacentStations = new ArrayList<>();
        for (TrolleyRoute route : routes) {
            if (route.getFromStation().equals(stationName)) {
                adjacentStations.add(route.getToStation());
            } else if (route.getToStation().equals(stationName)) {
                adjacentStations.add(route.getFromStation());
            }
        }
        return adjacentStations;
    }

    // Get the weight of a route between two stations
    public int getRouteWeight(String fromStation, String toStation) {
        // todo: Calculate the route weight between stations
        for (TrolleyRoute route : routes) {
            if (route.getFromStation().equals(fromStation) && route.getToStation().equals(toStation)
            || route.getFromStation().equals(toStation) && route.getToStation().equals(fromStation)) {
                return route.getWeight();
            }
        }
        return -1; // No direct route
    }

    // ********** Breadth First Search (BFS) ********** //
    public List<String> breadthFirstSearch(String startStation, String endStation) {
        // todo: Implement a BFS (see readme)
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();

        queue.add(startStation);
        visited.add(startStation);
        while (!queue.isEmpty()) {
            String currentStation = queue.poll();

            if (currentStation.equals(endStation)) {
                return reconstructPath(parent, startStation, endStation);
            }

            for (String adjacentStation : getAdjacentStations(currentStation)) {
                if (!visited.contains(adjacentStation)) {
                    visited.add(adjacentStation);
                    parent.put(adjacentStation, currentStation);
                    queue.add(adjacentStation);
                }
            }
        }
        return null; // No path found
    }

    // ********** Depth First Search (DFS) ********** //
    public List<String> depthFirstSearch(String startStation, String endStation) {
        // todo: Implement a DFS (see readme)
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        if (dfsHelper(startStation, endStation, visited, parent)) {
            return reconstructPath(parent, startStation, endStation);
        }
        return null; // No path found
    }

    private boolean dfsHelper(String currentStation, String endStation, Set<String> visited, Map<String, String> parent) {
        if (currentStation.equals(endStation)) {
            return true;
        }

        visited.add(currentStation);
        for (String adjacentStation : getAdjacentStations(currentStation)) {
            if (!visited.contains(adjacentStation)) {
                parent.put(adjacentStation, currentStation);
                if (dfsHelper(adjacentStation, endStation, visited, parent)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ********** Dijkstra's Algorithm ********** //
    public List<String> dijkstra(String startStation, String endStation) {
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        // Priority queue to select the station with the smallest distance
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distance::get));

        // Initialize distances to all stations as infinity
        for (TrolleyStation station : stations) {
            distance.put(station.getName(), Integer.MAX_VALUE);
        }
        distance.put(startStation, 0);
        queue.add(startStation);

        while (!queue.isEmpty()) {
            String currentStation = queue.poll();

            if (visited.contains(currentStation)) continue;
            visited.add(currentStation);

            if (currentStation.equals(endStation)) {
                return reconstructPath(parent, startStation, endStation);
            }

            for (String neighbor : getAdjacentStations(currentStation)) {
                int weight = getRouteWeight(currentStation, neighbor);
                if (weight == -1) continue;

                int newDist = distance.get(currentStation) + weight;
                if (newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    parent.put(neighbor, currentStation);
                    queue.add(neighbor);
                }
            }
        }

        return null; // No path found
    }


    // Helper method to reconstruct the path from start to end using the parent map
    private List<String> reconstructPath(Map<String, String> parentMap, String start, String end) {
        List<String> path = new ArrayList<>();
        String currentStation = end;

        while (currentStation != null) {
            path.addFirst(currentStation);
            currentStation = parentMap.get(currentStation);
        }

        return path.size() > 1 ? path : null;
    }
}