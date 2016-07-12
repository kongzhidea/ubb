package com.kk.trie;

public class Test {
	public static void main(String[] args) {
		TrieTree tree = new TrieTree();
		tree.add("hello");
		tree.add("am");
		tree.add("kong");
		tree.add("zhi");
		tree.add("te");
		tree.add("tes");
		tree.add("st");
		tree.add("wo");
		tree.add("woshi");
		tree.add("shi");
		tree.add("tab");
		tree.add("tabc");
		tree.add("tabcd");
		tree.add("智慧");

		tree.compile();
		
//		TrieTree.trace(tree.getRoot());
		ReplaceStrategy s = new ReplaceStrategyImpl();
		String text = "你是?hello,i am kongzhihui,find a test,woshi,tabcde,孔智慧";
		String text2 = "wsi，你谁啊,tabc";

		System.out.println(tree.replaceOld(text, s));
		System.out.println(tree.replace(text, s));
		System.out.println(tree.containKeyWord(text2));
	}
}
