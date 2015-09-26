package app;

import java.io.CharArrayReader; 
import java.io.CharArrayWriter;
import java.io.Reader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

public class LuaScriptEngine {

   ScriptEngineManager scriptEngMgr = new ScriptEngineManager();
   ScriptEngine scriptEng = scriptEngMgr.getEngineByName("luaj");
   ScriptEngineFactory scriptEngFtry = scriptEng.getFactory();
   
   Globals globals = JsePlatform.standardGlobals();

   String evalStatus;
   
   LuaScriptEngine(String luaStartupFile) {
   
	  //LuaValue chunk = globals.loadfile(luaStartupFile);
      //chunk.call();
      globals.get("dofile").call( LuaValue.valueOf(luaStartupFile));
      
      LuaValue defineDefaults = globals.get("DefineDefaults");
      LuaValue retvals = defineDefaults.call();
      LuaValue x = globals.get("x");
      LuaValue y = globals.get("y");
      scriptEng.put("x", x);
      //print out the result from the lua function
      System.out.println(x.tojstring());
      System.out.println(y.tojstring());
      System.out.println(retvals.tojstring());

   } // End LuaScrptEngine()
   
   public String getVersionStr () {
	
	   return "Lua version: Engine = " + scriptEngFtry.getEngineVersion() + ", Language = "+ scriptEngFtry.getLanguageVersion();
			   
   } // End getVersion()
   

   public boolean evalStr(String statement) {
      
      boolean evalPassed = false;
      
      if (statement == null) System.out.println("Statement: null");
	  try {
		 System.out.println("Statement: " + statement); 
	     Object evalObj = scriptEng.eval(statement);
		 if (evalObj != null) System.out.println("Eval Result: " + evalObj.toString()); 
	     evalPassed = true;
      } catch ( ScriptException se ) {
      	 evalStatus = se.getMessage();
      }
	
	  return evalPassed;
			  
   } // End evalStr()
   
   public String getEvalStatus() {
	   
	   return evalStatus;
	   
   } // End evalStatus()
   
} // End Class LuaScriptEngine
