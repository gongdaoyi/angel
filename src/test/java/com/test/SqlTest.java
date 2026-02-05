package com.test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlTest {

    public static void main(String[] args) {
        // 获取sql中全部字段名
        getFields();
    }

    private static void getFields() {
        String input = "select * from hs_asset.acctrelationship where (${matchBusinType} = ' ' or match_busin_type= ${matchBusinType}) and (${mainAcct} = ' ' or main_acct = ${mainAcct}) and (${relationshipAcct} = ' ' or relationship_acct = ${relationshipAcct})";
        String regex = "\\$\\{([^}]*)}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        Set<String> list = new HashSet<>();
        while (matcher.find()) {
            list.add(matcher.group(1));
        }

        System.out.println(list);
    }
}
