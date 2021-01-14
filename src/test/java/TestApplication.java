import cn.hutool.core.util.StrUtil;
import com.github.davidfantasy.mybatisplus.generatorui.GeneratorConfig;
import com.github.davidfantasy.mybatisplus.generatorui.MybatisPlusToolsApplication;
import com.github.davidfantasy.mybatisplus.generatorui.mbp.NameConverter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Locale;

public class TestApplication {


    public static void main(String[] args) {
        GeneratorConfig config = GeneratorConfig.builder().jdbcUrl("jdbc:mysql://localhost:3306/testdb")
                .userName("root")
                .password("root")
                .port(8068)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .basePackage("com.github.davidfantasy.mybatisplus.generatorui.example")
                .nameConverter(new NameConverter() {
                    @Override
                    public String serviceNameConvert(String tableName) {
                        return this.entityNameConvert(tableName) + "Service";
                    }

                    @Override
                    public String controllerNameConvert(String tableName) {
                        return this.entityNameConvert(tableName) + "Action";
                    }
                })
                .build();
        MybatisPlusToolsApplication.run(config);
    }

    @Test
    public void getTableName(){
        String name = "gen_FORM_conf";
        name = name.replace(StrUtil.UNDERLINE,StrUtil.EMPTY).toLowerCase();
        System.out.println(name);
    }


}
