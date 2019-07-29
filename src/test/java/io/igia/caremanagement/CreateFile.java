/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v.
 * 2.0 with a Healthcare Disclaimer.
 * A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
 * be found under the top level directory, named LICENSE.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * If a copy of the Healthcare Disclaimer was not distributed with this file, You
 * can obtain one at the project website https://github.com/igia.
 *
 * Copyright (C) 2018-2019 Persistent Systems, Inc.
 */
package io.igia.caremanagement;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CreateFile {
    
    public static void createFile(String xmlResponse, String filePath) throws IOException
    {
        System.out.println(xmlResponse);
        File file = new File(filePath);
        if(!file.exists()){
            file.createNewFile();
         }
        FileWriter fw = new FileWriter(file,false);
        fw.write(xmlResponse);
        fw.flush();
        fw.close();
    }
    
    public static void createRead(String filePath) throws IOException
    {
        File response = new File(filePath);
        FileReader fr = new FileReader(response);
        System.out.println(fr.read());
        fr.close();
        
        
    }
}
