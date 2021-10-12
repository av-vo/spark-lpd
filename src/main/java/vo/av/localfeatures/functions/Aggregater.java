package vo.av.localfeatures.functions;

import org.apache.spark.api.java.function.Function2;

import java.util.List;

public class Aggregater {

    /*
    public static Function2<List<Point>, Point, List<Point>> addPointToList(){
        return (Function2<List<Point>, Point, List<Point>>) (list, point) -> {
                list.add(point);
                return list;
        };
    }

    public static Function2<List<Point>, List<Point>, List<Point>> agreegateTwoLists(){
        return (Function2<List<Point>, List<Point>, List<Point>>) (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };
    }*/

    public static <T> Function2<List<T>, T, List<T>> addItemToList(){
        return (Function2<List<T>, T, List<T>>) (list, point) -> {
            list.add(point);
            return list;
        };
    }

    public static <T> Function2<List<T>, List<T>, List<T>> agreegateTwoLists(){
        return (Function2<List<T>, List<T>, List<T>>) (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };
    }

}

