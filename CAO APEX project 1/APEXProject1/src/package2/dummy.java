/*package package2;

public class dummy {

	private void decode() 
	{
		String currentDecode = null;
		
		
		if(!stalled)
		{
			fetchStalled = false;
			
			//System.out.println("Stalled: "+stalled);
			//System.out.println("Getting input from F");
			
			currentDecode = currentExecution.get("F");
			//System.out.println("Decode: "+currentDecode);
			currentExecution.put("D/RF",currentDecode);
		}
		
		else
		{
			//System.out.println("Stalled: "+stalled);
			//System.out.println("Getting input from D/RF");
			
			currentDecode = currentExecution.get("D/RF");
			//System.out.println("Decode: "+currentDecode);
			currentExecution.put("D/RF",currentDecode);
		}
		
		if(currentDecode != null)
		{
			
			if(!checkDependency())
			{
				//System.out.println("No dependency");
				stalled = false;
				currentExecution.put("D/RF",currentDecode);
		
				String[] splitDecode = currentDecode.split(" ");
				
				if(splitDecode[0].equals("MOVC"))
				{
		//			System.out.println(splitDecode[0]);
					currentExecution.put("D/RF",currentDecode);
					
				}
				
				else if(splitDecode[0].equals("MOV"))
				{
					//System.out.println(splitDecode[0]);
					
					//System.out.println(splitDecode[2]);
					int src1 = registerFile.get(splitDecode[2]);
					//System.out.println(src1);
					
					StringBuilder decodeMove = new StringBuilder();
					decodeMove.append(splitDecode[0]);
					decodeMove.append(" ");
					decodeMove.append(splitDecode[1]);
					decodeMove.append(" ");
					decodeMove.append(src1);
					
					//System.out.println(decodeMove.toString());	
					currentExecution.put("D/RF",decodeMove.toString());				
				
				}
				
				else if
				(splitDecode[0].equals("ADD") || splitDecode[0].equals("SUB") || splitDecode[0].equals("MUL") 
				|| splitDecode[0].equals("AND") || splitDecode[0].equals("OR") || splitDecode[0].equals("EX-OR"))
				{
	//				System.out.println(splitDecode[0]);
					
					int src1 = registerFile.get(splitDecode[2]);
					int src2 = registerFile.get(splitDecode[3]);
					
					StringBuilder decodeRegister = new StringBuilder();
					decodeRegister.append(splitDecode[0]);
					decodeRegister.append(" ");
					decodeRegister.append(splitDecode[1]);
					decodeRegister.append(" ");
					decodeRegister.append(src1);
					decodeRegister.append(" ");
					decodeRegister.append(src2);
						
		//			System.out.println(decodeRegister.toString());
					currentExecution.put("D/RF",decodeRegister.toString());				
									
			
				}
				
				else if(splitDecode[0].equals("LOAD"))
				{
					//System.out.println(splitDecode[0]);
					
					int src1 = registerFile.get(splitDecode[2]);
					
					boolean loadFlag = false;
					
					int dest2 = 0;
					
					if(registerFile.containsKey(splitDecode[3]))
					{
						 dest2 = registerFile.get(splitDecode[3]);
						 loadFlag = true;
					}
					
					StringBuilder decodeLoad = new StringBuilder();
					decodeLoad.append("LOAD");
					decodeLoad.append(" ");
					decodeLoad.append(splitDecode[1]);
					decodeLoad.append(" ");
					decodeLoad.append(src1);
					decodeLoad.append(" ");
					
					//3rd operand is register
					if(loadFlag)
						decodeLoad.append(String.valueOf(dest2));
					
					//3rd operand is literal
					else
						decodeLoad.append(splitDecode[3]);
					
		//			System.out.println(decodeLoad.toString());
					currentExecution.put("D/RF",decodeLoad.toString());
					
					
				}
				
				else if(splitDecode[0].equals("STORE"))
				{
		//			System.out.println(splitDecode[0]);
					
					int src1 = registerFile.get(splitDecode[1]);
					int dest1 = registerFile.get(splitDecode[2]);
					
					boolean storeFlag = false;
					
					int dest2 = 0;
					
					if(registerFile.containsKey(splitDecode[3]))
					{
						 dest2 = registerFile.get(splitDecode[3]);
						 storeFlag = true;
					}
					
					 
					
					StringBuilder decodeStore = new StringBuilder();
					decodeStore.append("STORE");
					decodeStore.append(" ");
					decodeStore.append(src1);
					decodeStore.append(" ");
					decodeStore.append(dest1);
					decodeStore.append(" ");
					
					//3rd operand is register
					if(storeFlag)
						decodeStore.append(String.valueOf(dest2));
					
					//3rd operand is literal
					else
					decodeStore.append(splitDecode[3]);
					
		//			System.out.println(decodeStore.toString());
					currentExecution.put("D/RF",decodeStore.toString());
					
					
				}
				else if(splitDecode[0].equals("JUMP"))
				{
					int src1 = registerFile.get(splitDecode[1]);
					
					StringBuilder decodeJump = new StringBuilder();
					decodeJump.append("JUMP");
					decodeJump.append(" ");
					decodeJump.append(src1);
					decodeJump.append(" ");
					decodeJump.append(splitDecode[2]);					
					
			//		System.out.println(decodeJump.toString());
					currentExecution.put("D/RF",decodeJump.toString());
				}
				else if(splitDecode[0].equals("BAL"))
				{
					int src1 = registerFile.get(splitDecode[1]);
					
					StringBuilder decodeBal = new StringBuilder();
					decodeBal.append("BAL");
					decodeBal.append(" ");
					decodeBal.append(src1);
					decodeBal.append(" ");
					decodeBal.append(splitDecode[2]);
									
			//		System.out.println(decodeBal.toString());
					currentExecution.put("D/RF",decodeBal.toString());
				}
			}//end if check for dependency
			else
			{
			//		System.out.println("Dependency is there");
					currentExecution.put("D/RF",currentDecode);
					stalled = true;
			}
			
		}//end if for null
			
		
			fetch();
	}

	
	
	
}
*/