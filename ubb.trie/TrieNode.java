package com.kk.trie;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Trie树中的节点
 */
public class TrieNode {

	/**
	 * 子节点
	 */
	private Map<Character, TrieNode> children;

	/**
	 * 失败指针
	 */
	private TrieNode fail;

	/**
	 * 当匹配到些节点为止时，匹配到的关键字
	 */
	private Set<String> results = new HashSet<String>();

	public TrieNode() {
		this.children = new LinkedHashMap<Character, TrieNode>();
	}

	/**
	 * 扩展枝条
	 */
	public TrieNode extend(char[] chars) {
		TrieNode node = this;
		for (int i = 0; i < chars.length; i++) {
			node = node.touchChild(chars[i]);
		}
		return node;
	}

	/**
	 * 有则返回，没有则创建后再返回
	 */
	private TrieNode touchChild(char ch) {
		TrieNode child = this.children.get(ch);
		if (child != null) {
			return child;
		}

		TrieNode next = new TrieNode();
		this.children.put(ch, next);
		return next;
	}

	public TrieNode get(char key) {
		return this.children.get(key);
	}

	public void put(char key, TrieNode value) {
		this.children.put(key, value);
	}

	public char[] keys() {
		char[] result = new char[children.size()];
		int i = 0;
		for (Character c : children.keySet()) {
			result[i] = c;
			i++;
		}
		return result;
	}

	public TrieNode getFail() {
		return this.fail;
	}

	public void setFail(TrieNode f) {
		this.fail = f;
	}

	public void addResult(String result) {
		this.results.add(result);
	}

	public Collection<String> getResults() {
		return this.results;
	}

}