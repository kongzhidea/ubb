package com.kk.trie;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * <pre>
 * Trie树又称单词查找树，字典树，是实现AC自动机算法的关键数据结构。
 * AC自动机算法分为3步：
 * (1)构造一棵Trie树;
 * (2)构造失败指针;
 * (3)根据AC自动机，搜索待处理的文本。
 * 
 * Trie树有3个基本性质：
 * (1) 根节点不包含字符，除根节点外每一个节点都只包含一个字符； 
 * (2) 从根节点到某一节点，路径上经过的字符连接起来，为该节点对应的字符串； 
 * (3) 每个节点的所有子节点包含的字符都不相同。
 * 
 * 搜索的方法为:
 * (1) 从根节点开始一次搜索； 
 * (2) 取得要查找关键词的第一个字符，并根据该字符选择对应的子树并转到该子树继续进行检索； 
 * (3) 在相应的子树上，取得要查找关键词的第二个字符,并进一步选择对应的子树进行检索。 
 * (4) 迭代过程…… 
 * (5) 在某个节点处，关键词的所有字符已被取出，则读取附在该节点上的信息，即完成查找。
 * </pre>
 * 
 */
public class TrieTree {

	private TrieNode root;

	private boolean compiled = false;

	private Set<Character> skipChars = new HashSet<Character>();

	public TrieTree() {
		this.root = new TrieNode();
	}

	/**
	 * 构建Trie树，作为搜索的数据结构。
	 * 
	 * @param keyword
	 *            关键字字符串
	 */
	public void add(String keyword) {
		if (null == keyword || keyword.trim().isEmpty()) {
			throw new IllegalArgumentException("过滤关键词不能为空！");
		}
		if (compiled) {
			throw new IllegalStateException("TrieTree编译后不能再添加关键字");
		}
		TrieNode last = this.root.extend(keyword.toCharArray());
		last.addResult(keyword);
	}

	public void addSkipChar(char ch) {
		this.skipChars.add(ch);
	}

	public void addSkipChar(Collection<Character> chars) {
		if (null != chars) {
			this.skipChars.addAll(chars);
		}
	}

	/**
	 * 编译Trie树
	 */
	public void compile() {
		this.buildFailPath();
		this.compiled = true;
	}

	/**
	 * 出现重叠的keyword会有问题,例如 ab,abc或者 ab,cab同时存在，推荐使用replace
	 * 
	 * @param text
	 * @param s
	 * @return
	 */
	public String replaceOld(String text, ReplaceStrategy s) {
		checkNotNull(text, "Null value not allowed for parameter 'text'");
		checkNotNull(s, "Null value not allowed for parameter 'strategy'");

		StringBuilder ret = new StringBuilder();
		char[] chars = text.toCharArray();
		TrieNode last = this.root;
		int lastIndex = 0;
		/* 保留上一字符匹配到的字符串，比如心事、心事重、心事重重 */
		String preKeyword = null;
		/* 已匹配的关键词个数 */
		int cnt = 0;
		for (int i = lastIndex; i < chars.length; i++) {
			char ch = chars[i];
			if (skipChars.contains(ch)) {
				continue;
			}
			while (last.get(ch) == null) {
				if (last == this.root) {
					break;
				}
				last = last.getFail();
			}
			last = last.get(ch);
			if (null == last) {
				last = root;
			}
			if (!last.getResults().isEmpty()) {
				preKeyword = last.getResults().iterator().next();
				cnt++;
				// 已经匹配到主串的最后一个字符，直接替换
				if (i == chars.length - 1) {
					ret.setLength(ret.length() - (preKeyword.length() - cnt));
					ret.append(s.replaceWith(preKeyword));
				}
			} else if (preKeyword != null) {
				ret.setLength(ret.length() - (preKeyword.length() - cnt));
				ret.append(s.replaceWith(preKeyword));
				ret.append(ch);
				// 重置状态
				preKeyword = null;
				cnt = 0;
			} else {
				ret.append(ch);
			}
		}

		return ret.toString();
	}

	/**
	 * 最小匹配，即能匹配的先匹配完
	 * 
	 * @param text
	 * @param s
	 * @return
	 */
	public String replace(String text, ReplaceStrategy s) {
		checkNotNull(text, "Null value not allowed for parameter 'text'");
		checkNotNull(s, "Null value not allowed for parameter 'strategy'");

		StringBuilder ret = new StringBuilder();
		char[] chars = text.toCharArray();
		TrieNode last = this.root;
		int lastIndex = 0;
		/* 保留上一字符匹配到的字符串，比如心事、心事重、心事重重 */
		String preKeyword = null;
		for (int i = lastIndex; i < chars.length; i++) {
			char ch = chars[i];
			if (skipChars.contains(ch)) {
				continue;
			}
			while (last.get(ch) == null) {
				if (last == this.root) {
					break;
				}
				last = last.getFail();
			}
			last = last.get(ch);
			if (null == last) {
				last = root;
			}
			if (!last.getResults().isEmpty()) {
				preKeyword = last.getResults().iterator().next();
				// 已经匹配到主串的最后一个字符，直接替换
				ret.setLength(ret.length() - preKeyword.length() + 1);
				ret.append(s.replaceWith(preKeyword));
				last = root;
			} else {
				ret.append(ch);
			}
		}

		return ret.toString();
	}

	/**
	 * 判断是否含有关键词 
	 * 
	 * @param text
	 * @return
	 */
	public boolean containKeyWord(String text) {
		checkNotNull(text, "Null value not allowed for parameter 'text'");

		char[] chars = text.toCharArray();
		TrieNode last = this.root;

		/* 已匹配的关键词个数 */
		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			if (skipChars.contains(ch)) {
				continue;
			}
			while (last.get(ch) == null) {
				if (last == this.root) {
					break;
				}
				last = last.getFail();
			}
			last = last.get(ch);
			if (null == last) {
				last = root;
			}
			if (!last.getResults().isEmpty()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * <pre>
	 * 构造失败指针的过程概括起来就一句话：设这个节点上的字母为x，沿着他父亲的失败指针走，直到走到一个节点，他的儿子中也有字母为x的节点。
	 * 然后把当前节点的失败指针指向那个字符也为x的儿子。如果一直走到了root都没找到，那就把失败指针指向root。
	 * 有两个规则：
	 * (1)root的子节点的失败指针都指向root。
	 * (2)节点(字符为x)的失败指针指向：从X节点的父节点的fail节点回溯直到找到某节点的子节点也是字符x，没有找到就指向root。
	 * </pre>
	 */
	private void buildFailPath() {
		Deque<TrieNode> nodes = new LinkedList<TrieNode>();
		// 第二层要特殊处理，将这层中的节点的失败路径直接指向父节点(也就是根节点)。
		for (char ch : this.root.keys()) {
			TrieNode child = this.root.get(ch);
			child.setFail(this.root);
			nodes.add(child);
		}

		while (!nodes.isEmpty()) {
			TrieNode node = nodes.pop();
			char[] keys = node.keys();
			for (int i = 0; i < keys.length; i++) {
				TrieNode r = node;

				char ch = keys[i];
				TrieNode child = r.get(ch);
				nodes.add(child);

				r = r.getFail();
				while (null != r && r.get(ch) == null) {
					r = r.getFail();
				}
				if (null == r) {
					child.setFail(this.root);
				} else {
					child.setFail(r.get(ch));
//					r.getResults().addAll(r.get(ch).getResults());
				}

			}
		}
	}

	private void checkNotNull(Object o, String msg) {
		if (o == null) {
			throw new NullPointerException(msg);
		}
	}

	public TrieNode getRoot() {
		return root;
	}

	public static void trace(TrieNode root) {
		if (root == null) {
			return;
		}
		System.out.println(root.getResults() + ".." + new String(root.keys()));
		for (char ch : root.keys()) {
			trace(root.get(ch));
		}
	}

}
