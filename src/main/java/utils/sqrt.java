package utils;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

public class sqrt extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        int val = (int) ((NumberTerm) args[1]).solve();
        int result = (int) Math.sqrt(val);
        return un.unifies(args[0], new NumberTermImpl(result));
    }
}
