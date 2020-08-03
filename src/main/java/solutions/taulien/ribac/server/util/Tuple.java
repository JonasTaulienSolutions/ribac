package solutions.taulien.ribac.server.util;

import io.reactivex.functions.Function;

public class Tuple<L, R> {

    private final L left;

    private final R right;



    public static <L, R> Tuple<L, R> of(L left, R right) {
        return new Tuple<>(left, right);
    }



    public static <L, R> Function<R, Tuple<L, R>> of(L left) {
        return (R right) -> Tuple.of(left, right);
    }



    private Tuple(L left, R right) {
        this.left = left;
        this.right = right;
    }



    public L getLeft() {
        return left;
    }



    public R getRight() {
        return right;
    }



    public <NewL> Tuple<NewL, R> setLeft(NewL newLeft) {
        return Tuple.of(newLeft, this.right);
    }



    public <NewR> Tuple<L, NewR> setRight(NewR newRight) {
        return Tuple.of(this.left, newRight);
    }



    public <NewR> Tuple<L, NewR> modifyRight(java.util.function.Function<R, NewR> modifier) {
        return this.setRight(modifier.apply(this.getRight()));
    }



    public <NewL> Tuple<NewL, R> modifyLeft(java.util.function.Function<L, NewL> modifier) {
        return this.setLeft(modifier.apply(this.getLeft()));
    }
}
