package ktplusplus.util;

import java.util.*;

public class FieldFrame {
    public enum Scope {
        CLASS,
        METHOD,
        INTERFACE,
        ANNOTATION,
    }

    public static class FrameEntry {
        private final Scope scope;
        private final String scopeID;
        private final String ident;

        FrameEntry(Scope declaredScope, String scopeID, String ident) {
            this.scope = declaredScope;
            this.scopeID = scopeID;
            this.ident = ident;
        }

        public Scope getScope() {
            return scope;
        }

        public String getIdent() {
            return ident;
        }

        public String getScopeID() {
            return scopeID;
        }

        @Override
        public String toString() {
            return "FrameEntry{" +
                    "scope=" + scope +
                    ", ident='" + ident + '\'' +
                    '}';
        }
    }

    private final Stack<Map<String, FrameEntry>> frame = new Stack<>();
    private final Stack<Scope> scopes = new Stack<>();
    private final Stack<String> scopeIDs = new Stack<>();

    public FrameEntry lookup(String ident) {
        for (Map<String, FrameEntry> entry : frame) {
            if (entry.containsKey(ident)) {
                return entry.get(ident);
            }
        }
        return null;
    }

    public List<String> getFields() {
        List<String> fields = new ArrayList<>();
        for (Map<String, FrameEntry> entry : frame) {
            for (FrameEntry frame : entry.values()) {
                if (frame.scope == Scope.CLASS
                        || frame.scope == Scope.INTERFACE
                        || frame.scope == Scope.ANNOTATION) {
                    fields.add(frame.ident);
                }
            }
        }
        return fields;
    }

    public Scope currentScope() {
        if (scopes.isEmpty()) {
            return null;
        }
        return scopes.peek();
    }

    public String currentScopeID() {
        if (scopeIDs.isEmpty()) {
            return null;
        }
        return scopeIDs.peek();
    }

    public Stack<Map<String, FrameEntry>> getState() {
        return this.frame;
    }

    public void addField(String ident) {
        frame.peek().put(ident, new FrameEntry(currentScope(), currentScopeID(), ident));
    }

    public void push(Scope scope, String scopeID) {
        this.scopes.push(scope);
        if (currentScopeID() == null) {
            this.scopeIDs.push(scopeID);
        } else {
            this.scopeIDs.push(currentScopeID() + "." + scopeID);
        }
        this.frame.push(new HashMap<>());
    }

    public void pop() {
        this.scopes.pop();
        this.scopeIDs.pop();
        this.frame.pop();
    }
}
