目前问题：



content中 包含4字节字符，无法识别，导致插入数据库失败。

例如：

correct string value: '\xF0\x9F\x98\x84\xE5\x93...'





处理办法:

先处理4字节字符， 转成对应的emoj  utf8编码

如下：

// 过滤4字节字符
remark = Emoj4byte2Char.convert(remark);
// 过滤emoji表情
remark = EmojiReplacer.replaceEmoji(remark);




Emoj4byte2Char  目前没有枚举到所有的情况，后续 遇到新的再补充就行。