package othello.ai.evaluators;

/**
 * Created by thijs on 11-4-2016.
 */
public abstract class MiniMaxEvaluator implements Evaluator{
    public final int maxDepth;
    MiniMaxEvaluator(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
