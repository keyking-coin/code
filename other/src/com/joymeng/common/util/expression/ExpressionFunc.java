package com.joymeng.common.util.expression;

public class ExpressionFunc {
	public static void ProtoExpressionInit() {
		ProtoExpression.RegisterFunction("getBuildLevel", new BaseFunc(){
			@Override
			public Object excute(Object... objects) throws Exception {
				if(objects.length == 0){
					return null;
				}
				
				return null;
			}
			
		});
		
		ProtoExpression.RegisterFunction("SlotUnlock", new BaseFunc(){
			@Override
			public Object excute(Object... objects) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		
		ProtoExpression.RegisterFunction("SoldierUnlock", new BaseFunc(){
			@Override
			public Object excute(Object... objects) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		
	}

}
