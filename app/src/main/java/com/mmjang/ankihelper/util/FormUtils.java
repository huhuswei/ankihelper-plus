package com.mmjang.ankihelper.util;

import com.cy.mdict.MdictUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FormUtils {

    public static ArrayList<String> getJForms(String key) throws IOException {
        File dataDir = StorageUtils.getFormsDirectory();
        com.cy.mdict.Mdict jFormsDict = new com.cy.mdict.Mdict(dataDir.getPath() + File.separator + "jforms.mdx", "");
        return MdictUtil.queryForms(key, jFormsDict);
    }
}
