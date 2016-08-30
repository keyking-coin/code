package com.joymeng.common.util.expression;

import java.math.BigDecimal;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.data.Guide;
import com.joymeng.slg.union.UnionBody;

public class CustomExpression implements Instances{
	   
	    public  Object progressFromBool(Object[] args) throws Exception
	    {
	        String[] argNames = new String[] { "boolean" };
	        ProtoExpression.CheckArgCount("progressFromBool", args, argNames);
	        return (!((boolean)args[0]) ? 0f : 100f);
	    }

	    public  Object progressFromFloat(Object[] args) throws Exception
	    {
	        String[] argNames = new String[] { "float", "target" };
	        ProtoExpression.CheckArgCount("progressFromFloat", args, argNames);
	        float num = (float)(args[0]);
	        float num2 = (float)(args[1]);
	        
	        if (num2 < (num + 0.001f))
	        {
	            return 100.0f;
	        }
	        float num3 = (num / num2)*100.0f;
	        if( num3 < 0.0f){
	        	return 0.0f;
	        }else if( num3 > 99.9f){
	        	return 99.9f;
	        }
	        else{
	        	return num3;
	        }
//	        return Math.clamp((float)(num3 * 100f), (float)0f, (float)99.9f);
	    }

	    public static void ProtoExpressionInit()
	    {
	    	ProtoExpression.RegisterFunction("min", new BaseFunc(){
	    		@Override
	    		public Object excute(Object... args) throws Exception{
					String[] argNames = new String[] { "a", "b" };
			        ProtoExpression.CheckArgCount("min", args, argNames);
			        if ((args[0] instanceof Integer) && (args[1] instanceof Integer))
			        {
			            return Math.min((int)args[0], (int)args[1]);
			        }
			        if ((args[0] instanceof Float) && (args[1] instanceof Integer))
			        {
			            return Math.min((float)args[0], (float)((int)args[1]));
			        }
			        if ((args[0] instanceof Integer) && (args[1] instanceof Float))
			        {
			            return Math.min((float)((int)args[0]), (float)args[1]);
			        }
			        if (!(args[0] instanceof Float) || !(args[1] instanceof Float))
			        {
			            throw new Exception("min only works with int or float arguments.");
			        }
			        return Math.min((float)args[0], (float)args[1]);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("max", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        String[] argNames = new String[] { "a", "b" };
	    	        ProtoExpression.CheckArgCount("max", args, argNames);
	    	        if ((args[0] instanceof Integer) && (args[1] instanceof Integer))
	    	        {
	    	            return Math.max((int)args[0], (int)args[1]);
	    	        }
	    	        if ((args[0] instanceof Float) && (args[1] instanceof Integer))
	    	        {
	    	            return Math.max((float)args[0], (float)((int)args[1]));
	    	        }
	    	        if ((args[0] instanceof Integer) && (args[1] instanceof Float))
	    	        {
	    	            return Math.max((float)((int)args[0]), (float)args[1]);
	    	        }
	    	        if( args[0] instanceof Double || args[1] instanceof Double){
	    	        	float arg1;
	    	        	float arg2;
	    	        	if(args[0] instanceof Double){
	    	        		arg1 = (float)((double)args[0]);
	    	        	}else if( args[0] instanceof Integer){
	    	        		arg1 = (float)((int)args[0]);
	    	        	}else if( args[0] instanceof Float){
	    	        		arg1 = (float)args[0];
	    	        	}else{
	    	        		throw new Exception("max only works with int or float arguments.");
	    	        	}
	    	        	if(args[1] instanceof Double){
	    	        		arg2 = (float)((double)args[1]);
	    	        	}else if( args[1] instanceof Integer){
	    	        		arg2 = (float)((int)args[1]);
	    	        	}else if( args[1] instanceof Float){
	    	        		arg2 = (float)args[1];
	    	        	}else{
	    	        		throw new Exception("max only works with int or float arguments.");
	    	        	}
	    	        	return Math.max(arg1, arg2);
	    	        }
	    	        if (!(args[0] instanceof Float) || !(args[1] instanceof Float))
	    	        {
	    	            throw new Exception("max only works with int or float arguments.");
	    	        }
	    	        return Math.max((float)args[0], (float)args[1]);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("abs", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    			   String[] argNames = new String[] { "input" };
	    		        ProtoExpression.CheckArgCount("abs", args, argNames);
	    		        float f = (float) args[0];//Convert.ToSingle(args[0]);
	    		        return Math.abs(f);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("floor", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    			  String[] argNames = new String[] { "input" };
	    		        ProtoExpression.CheckArgCount("floor", args, argNames);
	    		        float f = (float)args[0];
	    		        return Math.floor(f);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("ceil", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    			   String[] argNames = new String[] { "input" };
	    		        ProtoExpression.CheckArgCount("ceil", args, argNames);
	    		        float f = (float)args[0];
	    		        return Math.ceil(f);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("power", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    			   String[] argNames = new String[] { "f", "p" };
	    		        ProtoExpression.CheckArgCount("pow", args, argNames);
	    		        float f = (float)args[0];
	    		        float p = (float)args[1];
	    		        return Math.pow(f, p);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("exp", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    			   String[] argNames = new String[] { "power" };
	    		        ProtoExpression.CheckArgCount("exp", args, argNames);
	    		        float power = (float)args[0];
	    		        return Math.exp(power);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("log", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    			 String[] argNames = new String[] { "f", "p" };
	    		        ProtoExpression.CheckArgCount("log", args, argNames);
	    		        float f = (float)args[0];
	    		        float p = (float)args[1];
	    		        return Math.log(f)/Math.log(p);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("log10", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    			 String[] argNames = new String[] { "f" };
	    		        ProtoExpression.CheckArgCount("log10", args, argNames);
	    		        float f = (float)(args[0]);
	    		        return Math.log10(f);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("round", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        String[] argNames = new String[] { "value", "digits" };
	    	        ProtoExpression.CheckArgCount("round", args, argNames);
	    	        float num = (float)args[0];
	    	        float num2 = (float)args[1];
//	    	        return (float)Math.Round((double)num, (int)num2);
	    	        BigDecimal bg = new BigDecimal(num);
	    	        return bg.setScale((int)num2, BigDecimal.ROUND_HALF_UP).floatValue();
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("roundup", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        String[] argNames = new String[] { "number", "digits" };
	    	        ProtoExpression.CheckArgCount("roundup", args, argNames);
	    	        float num = (float)args[0];
	    	        float num2 = (float)args[1];
	    	        return (float)RoundUp((double)num, (int)num2);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("lookup", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        if (args.length < 3)
	    	        {
	    	            throw new Exception("lookup takes 3 or more arguments: index, value[0], value[1], ..., value[n]");
	    	        }
	    	        int index = 1 + ((int)args[0]);
	    	        if (index < 1)
	    	        {
	    	            index = 1;
	    	        }
	    	        else if (index > (args.length - 1))
	    	        {
	    	            index = args.length - 1;
	    	        }
	    	        return args[index];
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("nearestfloor", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        int num;
	    	        if (args.length < 3)
	    	        {
	    	            throw new Exception("nearestfloor takes 3 or more arguments: input, floor0, floor1, ...");
	    	        }
	    	        if (args[0] instanceof Float)
	    	        {
	    	            num = (int)((float)args[0]);
	    	        }
	    	        else
	    	        {
	    	            num = (int)args[0];
	    	        }
	    	        int num2 = num;
	    	        int num3 = 0x7fffffff;
	    	        int num4 = (int)args[1];
	    	        for (int i = 1; i < args.length; i++)
	    	        {
	    	            int num6 = (int)args[i];
	    	            if (num6 < num4)
	    	            {
	    	                num4 = num6;
	    	            }
	    	            int num7 = num - num6;
	    	            if ((num7 >= 0) && (num7 < num3))
	    	            {
	    	                num2 = num6;
	    	                num3 = num7;
	    	            }
	    	        }
	    	        if (num3 == 0x7fffffff)
	    	        {
	    	            return num4;
	    	        }
	    	        return num2;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("concat", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        if (args.length < 2)
	    	        {
	    	            throw new Exception("You must pass at least two values to concat, but they can be any type (and will be converted to strings automatically).");
	    	        }
	    	        
	    	        String reStr = new String();
	    	        for(int i=0;i<args.length;i++){
	    	        	reStr += args[i] + " ";
	    	        }
	    	        return reStr;//String.Concat(args);
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("if", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    			  String[] argNames = new String[] { "test", "resultIfTrue", "resultIfFalse" };
	    		        ProtoExpression.CheckArgCount("If", args, argNames);
	    		        if ( (boolean)args[0] )
	    		        {
	    		            return args[1];
	    		        }
	    		        return args[2];
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("isIOS", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    			 ProtoExpression.CheckArgCount("isIOS", args, new String[0]);
	    		     return false;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("isAndroid", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    			   ProtoExpression.CheckArgCount("isAndroid", args, new String[0]);
	    		       return true;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("deviceModelContains", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        if (args.length < 1)
	    	        {
	    	            throw new Exception("deviceModelContains requires at least one modelName string to look for.");
	    	        }
	    	        return false;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("needBuildingLevel", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        String[] argNames = new String[] { "level" };
	    	        ProtoExpression.CheckArgCount("NeedBuildingLevel" , args , argNames);
	    	        
	    	        //...
	    	        
	    	        boolean flag = true;
	    	        return flag;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("getBuildingLevel", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args){
	    			//...
	    			
	    	        int level = 1;
	    	        return level;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("needPlayerLevel", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        String[] argNames = new String[] { "level" };
	    	        ProtoExpression.CheckArgCount("NeedPlayerLevel", args, argNames);

	    	        boolean flag = true;
	    	        return flag;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("getPlayerLevel", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args){
	    	        int level = 1;
	    	        return level;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("getBuildingState", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args){
	    			   int buildingState = 0;
	    		        return buildingState;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("isAreaUnlock", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        String[] argNames = new String[] { "areaID" };
	    	        ProtoExpression.CheckArgCount("IsAreaUnlock", args, argNames);

	    	        boolean flag = true;
	    	        return flag;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("needMoney", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    	        String[] argNames = new String[] { "money" };
	    	        ProtoExpression.CheckArgCount("NeedMoney", args, argNames);

	    	        boolean flag = true;
	    	        return flag;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("showView", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args) throws Exception{
	    		    String[] argNames = new String[] { "viewID" };
	    	        ProtoExpression.CheckArgCount("ShowView", args, argNames);
	    	        return 0;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("showMoreInfo", new BaseFunc(){
	    		@Override
	    		public Object excute(Object...args){
	    			 return 0;
	    		}
	    	});
	    	ProtoExpression.RegisterFunction("getLevel", new BaseFunc() {				
				@Override
				public Object excute(Object... args) throws Exception {
					if (args.length < 1) {
						GameLog.error("getLevel in expression is error!");
						return null;
					}
					if (args[0] == null) {
						GameLog.error("getLevel in expression uid is null");
						return null;
					}
					
					long uid=Long.parseLong(String.valueOf(args[0]));
					Role role = world.getRole(uid);
					if (role == null) {
						GameLog.error("role is null");
						return 0;
					}
					return Integer.valueOf(role.getLevel());
				}
			});
			ProtoExpression.RegisterFunction("getBuilding", new BaseFunc() {	
				@Override
				public Object excute(Object... args) throws Exception {
					if (args.length < 3) {
						GameLog.error("getBuilding in expression is error!");
						return 0;
					}
					if (args[0] == null) {
						GameLog.error("getBuilding in expression uid is null");
						return 0;
					}
					long uid = Long.parseLong(String.valueOf(args[0]));
					Role role = world.getRole(uid);
					if (role == null) {
						GameLog.error("getBuilding in expression role is null");
						return 0;
					}
					RoleCityAgent city = role.getCity(Integer.parseInt(args[1].toString()));
					int level = city.getBuildMaxLevel(args[2].toString());
					return Integer.valueOf(level);
				}
			});
			ProtoExpression.RegisterFunction("isPrevExpression", new BaseFunc() {	
				@Override
				public Object excute(Object... args) throws Exception {
					if (args.length < 2) {
						GameLog.error("isPrevExpression in expression is error!");
						return null;
					}
					if (args[0] == null || args[1] == null) {
						GameLog.error("isPrevExpression in expression is null");
						return null;
					}
					long uid = Long.parseLong(String.valueOf(args[0]));
					Role role = world.getRole(uid);
					if (role == null) {
						GameLog.error("getRole is fail,value is null");
						return Boolean.FALSE;
					}
					String guideId = String.valueOf(args[1]);
					Guide guide = dataManager.serach(Guide.class, guideId);
					if (guide == null) {
						GameLog.error("read Guide basedata fail, result is null");
						return Boolean.FALSE;
					}
					List<String> guideIdList = role.getGuideIdList();
					for (int i = 0 ; i < guideIdList.size() ; i++){
						String tempGuideId = guideIdList.get(i);
						if (tempGuideId != null && tempGuideId.equals(guideId)) {
							return Boolean.TRUE;
						}
					}
					return Boolean.FALSE;
				}
			});
			ProtoExpression.RegisterFunction("GuideIsOpenDialog", new BaseFunc() {	
				@Override
				public Object excute(Object... args) throws Exception {
					return Boolean.TRUE;
				}
			});
			ProtoExpression.RegisterFunction("IsInCity", new BaseFunc() {	
				@Override
				public Object excute(Object... args) throws Exception {
					return Boolean.TRUE;
				}
			});
			ProtoExpression.RegisterFunction("IsInWorld", new BaseFunc() {	
				@Override
				public Object excute(Object... args) throws Exception {
					return Boolean.TRUE;
				}
			});
			ProtoExpression.RegisterFunction("IsHasItem", new BaseFunc() {	
				@Override
				public Object excute(Object... args) throws Exception {
					return Boolean.TRUE;
				}
			});
			ProtoExpression.RegisterFunction("IsHasBattleReporter", new BaseFunc() {	
				@Override
				public Object excute(Object... args) throws Exception {
					return Boolean.TRUE;
				}
			});
			ProtoExpression.RegisterFunction("IsPowerLack", new BaseFunc() {	
				@Override
				public Object excute(Object... args) throws Exception {
					return Boolean.TRUE;
				}
			});
			ProtoExpression.RegisterFunction("IsResourceLack", new BaseFunc() {	
				@Override
				public Object excute(Object... args) throws Exception {
					return Boolean.TRUE;
				}
			});
			ProtoExpression.RegisterFunction("IsMissionComplete", new BaseFunc() {	
				@Override
				public Object excute(Object... args) throws Exception {
					return Boolean.TRUE;
				}
			});
	    	ProtoExpression.RegisterFunction("getFight", new BaseFunc() {				
				@Override
				public Object excute(Object... args) throws Exception {
					if (args.length < 1) {
						GameLog.error("getFight in expression uid is null");
						return 0;
					}
					long uid = Long.parseLong(String.valueOf(args[0]));
					Role role = world.getRole(uid);
					if (role == null) {
						GameLog.error("role is null");
						return 0;
					}
					return Integer.valueOf(role.getFightPower());
				}
			});
	    	ProtoExpression.RegisterFunction("getUnionCity", new BaseFunc() {				
				@Override
				public Object excute(Object... args) throws Exception {
					if (args.length < 1) {
						GameLog.error("getUnionCity in expression unionId is null");
						return Integer.valueOf(0);
					}
					long unionId = Long.parseLong(String.valueOf(args[0]));
					List<MapUnionCity> citys = mapWorld.searchUnionCity(unionId);
					if (citys != null){
						int maxLevel = 0;
						for (int i = 0 ; i < citys.size() ; i++){
							MapUnionCity city = citys.get(i);
							if (city.getLevel() > maxLevel){
								maxLevel = city.getLevel();
							}
						}
						return Integer.valueOf(maxLevel);
					}
					return Integer.valueOf(0);
				}
			});
	    	ProtoExpression.RegisterFunction("getAllianceContr", new BaseFunc() {
				@Override
				public Object excute(Object... objects) throws Exception {
					if (objects.length < 1) {
						GameLog.error("getAllianceContr in expression unionId is null");
						return Integer.valueOf(0);
					}
					long unionId = Long.parseLong(String.valueOf(objects[0]));
					UnionBody union = unionManager.search(unionId);
					if (union == null){
						return Integer.valueOf(0);
					}
					return Integer.valueOf((int)union.getScore());
				}
	    	});
	    	ProtoExpression.RegisterFunction("getMoney", new BaseFunc() {				
				@Override
				public Object excute(Object... args) throws Exception {
					if (args.length < 1) {
						GameLog.error("getMoney in expression uid is null");
						return Integer.valueOf(0);
					}
					long uid = Long.parseLong(String.valueOf(args[0]));
					Role role = world.getRole(uid);
					if (role == null) {
						GameLog.error("role is null");
						return 0;
					}
					return Integer.valueOf(role.getMoney());
				}
			});
	    }
	    
	    private static  int sign( double num){
	    	if(num > 0.00000f){
	    		return 1;
	    	}else if(num < 0.00000f){
	    		return -1;
	    	}else{
	    		return 0;
	    	}
	    }

	    public static  double RoundUp(double num, int place)
	    {
	        double num2 = num * Math.pow(10.0, (double)place);
	        num2 = sign(num2) * Math.abs(Math.floor((double)(num2 + 0.5)));
	        return (num2 / Math.pow(10.0, (double)place));
	    }

	    public  Object IsBuildingSlotUnlock(Object[] args) throws Exception
	    {
	        String[] argNames = new String[] { "slotID" };
	        ProtoExpression.CheckArgCount("IsBuildingSlotUnlock", args, argNames);

	        boolean flag = true;
	        return flag;
	    }

}
