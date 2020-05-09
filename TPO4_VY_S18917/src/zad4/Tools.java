/**
 *
 *  @author Voiko Yehor S18917
 *
 */

package zad4;


import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Tools {
    static Options createOptionsFromYaml(String fileName) throws Exception{
        Yaml yaml = new Yaml();
        try(InputStream inputStream = Files.newInputStream(Paths.get(fileName))) {
            Map<String, Object> obj = yaml.load(inputStream);
            Options options = new Options((String)obj.get("host"), (Integer)obj.get("port"), (Boolean)obj.get("concurMode"), (Boolean)obj.get("showSendRes"),(Map<String, List<String>>)obj.get("clientsMap"));
            return options;
        }
    }
}
