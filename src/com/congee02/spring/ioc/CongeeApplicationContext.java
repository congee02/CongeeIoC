package com.congee02.spring.ioc;

import com.congee02.spring.annotation.Autowired;
import com.congee02.spring.annotation.Component;
import com.congee02.spring.annotation.ComponentScan;
import com.congee02.spring.annotation.Scope;
import com.congee02.spring.bean.BeanDefinition;
import com.congee02.spring.enums.ScopeEnum;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author congee(congee02 @ 163.com)
 * @date 1/24/2023 3:14 PM
 */
public class CongeeApplicationContext {

    private Class<?> configClazz;

    /**
     * 保证 singleton bean 的引用只指向一个对象，用于特殊处理 singleton bean 的 map
     */
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    /**
     * IoC 容器使用 map 存储
     */
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /** 根据配置类配置要组件扫描的包，根据 Component 注解 识别 bean，并特殊处理 singleton bean
     * @throws RuntimeException 注解扫描参数错误
     * @throws NullPointerException 注解扫描的包不存在
     * @param configClazz   配置类字节码
     */
    public CongeeApplicationContext(Class<?> configClazz) {

        this.configClazz = configClazz;

        /* 1 获取包对应的文件路径 */
        ComponentScan componentScan =
                configClazz.isAnnotationPresent(ComponentScan.class) ?
                        configClazz.getAnnotation(ComponentScan.class) : null;
        if (componentScan == null) {
            throw new RuntimeException("该配置类没有 ComponentScan 注解，无法进行组件扫描");
        }

        // 取得组件扫描包下类的Class对象，并根据是否带 Component 注解判断是否为 bean
        String packageName = componentScan.value();
        String packagePath = packageName.replace(".", "/");

        // 获取类加载器
        ClassLoader loader = this.getClass().getClassLoader();
        // 取得相对路径的文件夹
        URL resource = loader.getResource(packagePath);
        File file = new File(resource.getFile());
        if (! file.isDirectory()) {
            throw new RuntimeException("ComponentScan 参数必须为包名");
        }

        /* 2 遍历包对应目录下的 .class 文件，识别 bean，为其注册一个 BeanDefinition，并置入 beanDefinitionMap */
        File[] classFiles = file.listFiles();
        for (File classFile : classFiles) {
            // 忽略后缀不为 .class 的文件
            if (! classFile.getAbsolutePath().endsWith(".class")) {
                continue;
            }
            String className = classFile.getName().substring(0, classFile.getName().indexOf(".class"));
            // 获取类全限定名
            String fullyQualifiedName = packageName + "." + className;
            try {
                Class<?> clazz = loader.loadClass(fullyQualifiedName);
                // 检查有无 Component 注解。若没有 Component 注解，则说明该类不为 bean，忽略
                Component component =
                        clazz.isAnnotationPresent(Component.class) ? clazz.getAnnotation(Component.class) : null;
                if (component == null) {
                    continue;
                }
                // 设置 bean 的名称，若 Component 参数为空，则使用默认命名规则
                String beanName = component.value();
                beanName = "".equals(beanName) ? Introspector.decapitalize(clazz.getSimpleName()) : beanName;
                // 有 Component 注解，为 bean，为其注册一个 BeanDefinition
                BeanDefinition definition = new BeanDefinition();
                // 检查有无 Scope 注解，若无，默认为单例。若有，如果无参数，则默认为单例，否则为参数值
                definition.setScope(clazz.isAnnotationPresent(Scope.class) ?
                                    clazz.getAnnotation(Scope.class).value() :
                                    ScopeEnum.SINGLETON);
                // 设置 bean 类型
                definition.setType(clazz);
                // 置入 beanDefinitionMap
                beanDefinitionMap.put(beanName, definition);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        // 特殊处理 singleton bean
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition definition =
                    beanDefinitionMap.get(beanName);
            if (ScopeEnum.SINGLETON.equals(definition.getScope())) {
                Object bean = createBean(beanName, definition);
                singletonObjects.put(beanName, bean);
            }
        }

    }

    private Object createBean(String beanName, BeanDefinition definition) {

        Class clazz = definition.getType();

        try {
            Object instance = clazz.getConstructor().newInstance();

            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f : declaredFields) {
                if (f.isAnnotationPresent(Autowired.class)) {
                    f.setAccessible(true);
                    // fixme 对象属性名必须和 bean 的名称相同
                    f.set(instance, getBean(f.getName()));
                }
            }

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object getBean(String beanName) {

        BeanDefinition definition = beanDefinitionMap.get(beanName);
        if (definition == null) {
            throw new NullPointerException();
        }

        // 若为 singleton bean，从 singletonObjects 中取出并返回
        ScopeEnum scope = definition.getScope();
        if (ScopeEnum.SINGLETON.equals(scope)) {
            Object bean = singletonObjects.get(beanName);
            // 防止因为未知原因 bean 为 null 的情况
            if (bean == null) {
                Object o = createBean(beanName, definition);
                singletonObjects.put(beanName, bean);
                bean = o;
            }
            return bean;
        }
        return createBean(beanName, definition);
    }

}
