package io.rocketscience.java.collection;

import static io.rocketscience.java.lang.Lang.require;
import io.rocketscience.java.util.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Tree<T> {
	
	private final String id; // identifier, not necessarily unique
	private final T value;
	private final List<Tree<T>> children = new ArrayList<>();

	// needs to be accessible for attaching/detaching children
	Tree<T> parent = null;

	public Tree(String id) {
		this(id, null);
	}
	
	public Tree(String id, T value) {
		require(id != null, "id cannot be null");
		this.id = id;
		this.value = value;
	}
	
	public String getId() {
		return id;
	}
	
	public T getValue() {
		return value;
	}
	
	public List<Tree<T>> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public Tree<T> getParent() {
		return parent;
	}
	
	/**
	 * Attaches a node to this tree. Detaches the child from its current parent, if present.
	 * 
	 * @param child A Tree.
	 * @return true, if the child was attached, false if it was already attached to this tree.
	 */
	public boolean attach(Tree<T> child) {
		if (children.contains(child)) {
			return false;
		} else {
			child.parent = this;
			return children.add(child); // true
		}
	}
	
	/**
	 * Detaches a node from this tree.
	 * 
	 * @param child A Tree.
	 * @return true, if the child was detached, false if it is not a child of this tree.
	 */
	public boolean detach(Tree<T> child) {
		if (children.remove(child)) {
			child.parent = null;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isLeaf() {
		return children.size() == 0;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public Tree<T> getRoot() {
		return (parent == null) ? this : parent.getRoot();
	}

	// TODO: public List<Tree<T>> toList(Strategy strategy), where Strategy in { BreadthFirst, DepthFirst }
	
	/**
	 * Traverses a Tree top down, testing the given predicate against each tree node. If predicate.test() returns true,
	 * descend children, else go on with neighbors.
	 */
	public void traverse(Predicate<Tree<T>> predicate) {
		if (predicate.test(this)) {
			children.forEach(child -> child.traverse(predicate));
		}
	}

	/**
	 * Traverses a Tree top down, applying the given predicate to each tree node. If predicate.test() returns true, the
	 * tree node is part of the result list.
	 */
	public List<Tree<T>> collect(Predicate<Tree<T>> predicate) {
		final List<Tree<T>> result = new ArrayList<>();
		collect(predicate, result);
		return result;
	}

	private void collect(Predicate<Tree<T>> predicate, List<Tree<T>> result) {
		if (predicate.test(this)) {
			result.add(this);
		}
		children.forEach(child -> child.collect(predicate, result));
	}

	@Override
	public String toString() {
		return toString(0);
	}

	protected String toString(int depth) {
		final String indent = Strings.space(depth);
		final String inner = children.stream()
				.map(child -> child.toString(depth+1)) // create child strings
				.reduce((l,r) -> l + ",\n" + r) // concatenate child strings
				.map(s -> "\n" + s + "\n" + indent) // apply if concatenation is not empty
				.orElse("");
		final String content = (value == null) ? "" : value.toString().replaceAll("\\s+", " ").trim();
		return indent + id + "(" + content + inner + ")";
	}
	
}