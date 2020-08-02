package solutions.taulien.ribac.server.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface FunctionalHelper {

    static <ElementType, ResultType> List<ResultType> mapAll(
        final List<ElementType> list,
        Function<ElementType, ResultType> mapper
    ) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }


    static <ElementType, ResultType> Function<List<ElementType>, List<ResultType>> mapAll(
        Function<ElementType, ResultType> mapper
    ) {
        return (list) -> mapAll(list, mapper);
    }
}
