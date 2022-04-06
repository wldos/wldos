/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils;

import java.io.File;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * js脚本执行Java引擎，在Java后端执行js脚本的前后端打通工具。
 *
 * @author 树悉猿
 * @date 2021年3月11日
 * @version V1.0
 */
public class ScriptingAPI {

    public static void testScriptVariables(ScriptEngine engine) throws ScriptException{
        File file = new File("test.txt");
        engine.put("f", file);
        engine.eval("println('Total Space:'+f.getTotalSpace())");        
    }
    
    public static void testInvokeScriptMethod(ScriptEngine engine) throws Exception{
        String script = "function hello(name) { return 'Hello,' + name;}";
        engine.eval(script);
        Invocable inv = (Invocable) engine;
        String res = (String)inv.invokeFunction("hello", "Scripting" );
    }
    
    public static void testScriptInterface(ScriptEngine engine) throws ScriptException{
        String script = "var obj = new Object(); obj.run = function() { println('run method called'); }";
        engine.eval(script);
        Object obj = engine.get("obj");
        Invocable inv = (Invocable) engine;
        Runnable r = inv.getInterface(obj,Runnable.class);
        Thread th = new Thread(r);
        th.start();
    }
    
    public static void testUsingJDKClasses(ScriptEngine engine) throws Exception{
        //Packages是脚本语言里的一个全局变量,专用于访问JDK的package
        String js = "function doSwing(t){var f=new Packages.javax.swing.JFrame(t);f.setSize(400,300);f.setVisible(true);}";
        engine.eval(js);
        Invocable inv = (Invocable) engine;
        inv.invokeFunction("doSwing", "Scripting Swing" );
    }


    /**
     * 调用js脚本并传递java参数，返回执行结果
     * @param script
     * @param funName
     * @param params
     * @return
     * @throws ScriptException
     * @throws NoSuchMethodException
     */
    public static String invokeScript(String script, String funName, Object... params) throws ScriptException, NoSuchMethodException{
    	ScriptEngine engine = getJavaScriptEngine();
    	engine.eval(script);
        Invocable inv = (Invocable) engine;
        String res = (String)inv.invokeFunction(funName, params);
        
        return res;
    }

    /**
     * 获取js引擎
     * @return
     */
    protected static ScriptEngine getJavaScriptEngine(){
    	ScriptEngineManager manager = new ScriptEngineManager(); 
    	
        return manager.getEngineByName("JavaScript");
    }
}

