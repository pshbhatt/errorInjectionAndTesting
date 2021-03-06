
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Dumps {
    String useless="";
    String linesN = "";
	public static void main(String args[]) throws IOException {
		FileReader in = new FileReader("newDump");
		BufferedReader br = new BufferedReader(in);
		Dumps dumps  = new Dumps();
		
		ArrayList<String> totalList = new ArrayList<String>();
		ArrayList<String> list1 = new ArrayList<String>();
		ArrayList<String> instructionList = new ArrayList<String>();
		int lineNumber = 0;
		
			String line;
			int ct=0;
			while ((line = br.readLine()) != null) {
			    ct++;
			    if(ct>1){
			    totalList.add(line + "\n");
			    }
			}
			br.close();
			for(int x = 0;x<totalList.size();x++){
			    String opcode = totalList.get(x).substring(0,6);
			    String address = totalList.get(x).substring(totalList.get(x).length()-7,totalList.get(x).length());
			    if(opcode.equals("000011")){
				lineNumber = dumps.binDec(address.trim());
				lineNumber = lineNumber+ 1;
				break;
			    }
			    
			}
			
			for(int o=0;o<totalList.size();o++){
			    if(o>=lineNumber){
				list1.add(totalList.get(o));
			    }
			}
			DecimalFormat f = new DecimalFormat("##.#######");
			for(int i=0;i<100;i++){
			Random rand = new Random();
			double rate = rand.nextInt((10 - 2) + 1) + 2;
			rate = rate/10;
			System.out.println("Unreliability rate in PPM::" + f.format(rate/1000000) + "%");
			int modify = (int)(rate*(list1.size()*32));
			modify = modify/100;
			int maxAddress = list1.size() + lineNumber;
			String res = dumps.unreliable(lineNumber, i, modify, list1);
			rate = rand.nextInt((10 - 2) + 1) + 2;
			rate = rate/10;
			System.out.println("Insecurity rate in PPM::" + f.format(rate/1000000)+ "%");
			modify = (int)(rate*(list1.size()*32));
			modify = modify/100;
			dumps.insecure(lineNumber, i, maxAddress, modify, list1);
			
			}
			dumps.executeUI();			
		//} 
		}
	
	public String unreliable(int lineNumber, int count, int modify, ArrayList<String> list1) throws IOException{
	    Dumps dumps = new Dumps();
	    int copyBit = 0;
	    ArrayList<String>mipsList = new ArrayList<String>();
		mipsList = dumps.readMips(lineNumber);
		Random rand = new Random();
	    for(int z=0;z<modify;z++){
		int n = rand.nextInt(list1.size() * 32);
		double m = Math.floor(n / 32);
		int instructionLine = (int) m;
		int bit = n - (instructionLine * 32);
		String elements[] = list1.get(instructionLine).trim().split("");
		if (elements[bit].equals("0")) {
			elements[bit] = "1";
		} else {
			elements[bit] = "0";
		}
		String inst = "";
		for(int k = 0 ; k<elements.length;k++){
			inst = inst + elements[k];
		}
	    String output = dumps.checkFormat(inst + "!" + bit);
		if(output.equals("none")){
		   continue;
		} else {
		String[] outputArray = output.split("!");
		String opcode = outputArray[0];
		String field = outputArray[1];
		String value = outputArray[2];
		String filename = "unreliable_"+count;
		if(field.equals("US")){
		 File file = new File(filename);
		    dumps.copyFiles(file); 
		    copyBit = 1;
		    break;
		} 
		if(!field.equals("US")||!value.equals("US")){
		
		String mipsInst = mipsList.get(instructionLine);
		if(mipsInst.contains(":")){
		    mipsInst = mipsList.get(instructionLine+1);
		}
		String mipsOpcode = "";
		String fieldsArray[] = null;
		String field1="";
		String field2 = "";
		String field3="";
		String offset = "";
		
		if(!mipsInst.contains("jal")||!mipsInst.contains("j")){
		    if(!mipsInst.substring(0,1).equals(".")){
		    mipsOpcode = mipsInst.substring(0,mipsInst.indexOf("$"));
		    mipsOpcode = mipsOpcode.trim();
		    mipsInst = mipsInst.replace(mipsOpcode, "");
		    fieldsArray = mipsInst.split(",");
		    if(fieldsArray.length==2){
			if(mipsOpcode.equals("lw")||mipsOpcode.equals("sw")){
			    field1 = fieldsArray[0].trim();
			    offset = mipsInst.substring(mipsInst.indexOf("(")-1, mipsInst.indexOf("("));
			    field2 = mipsInst.substring(mipsInst.indexOf("(")+1, mipsInst.indexOf(")"));
			} else if(mipsOpcode.equals("li")){
			    field1 = fieldsArray[0].trim();
			    field2 = fieldsArray[1].trim();
			} else {
			    field1 = fieldsArray[0].trim();
				field2 = fieldsArray[1].trim();
			}
		    } else if(fieldsArray.length==3){
			field1 = fieldsArray[0].trim();
			field2 = fieldsArray[1].trim();
			field3 = fieldsArray[2].trim();
		    
		    
		} else {
		    mipsOpcode = mipsInst.substring(0,3);
		    mipsOpcode = mipsOpcode.trim();
		    mipsInst = mipsInst.replace(mipsOpcode, "");
		    fieldsArray[0] = mipsInst;
		    field1 = mipsInst;
		}
		String newInst = "";
		if(!(opcode.equals(mipsOpcode))){
		    if(fieldsArray.length==1){
			newInst = opcode + " " + field1;
		    }
		    if(field3.length()==0||field3.equals("N")){
		    newInst = opcode + " " +field1 + ", " +offset+"("+ field2+")\n";
		    } else {
			newInst = opcode + " " +field1 + ", " + field2 + ", " + field3+"\n"; 	
		    }
		} else if(field.equals("RR")){
		    field1 = value;
		    newInst = mipsOpcode + " " +field1 + ", " + field2 + ", " + field3+"\n";
		} else if(field.equals("R1")){
		    if(opcode.equals("lw")||opcode.equals("sw")||opcode.equals("li")){
			newInst = mipsOpcode + " " +field1 + ", " +offset+"("+ field2+")\n";
		    } else {
		    field2 = value;
		    newInst = mipsOpcode + " " +field1 + ", " + field2 + ", " + field3+"\n";
		    }
		} else if(field.equals("R2")){
		    if(opcode.equals("lw")||opcode.equals("sw")||opcode.equals("li")){
			newInst = mipsOpcode + " " +field1 + ", " +offset+"("+ field2+")\n";
		    } else {
		    field3 = value;
		    newInst = mipsOpcode + " "+field1 + ", " + field2 + ", " + field3+"\n";
		    }
		 }  else if(opcode.equals("sw") && field.equals("R2")){
		    field2 = value;
		    newInst = mipsOpcode + " "+field1 + ", " + offset+"("+field2+")"+"\n";
		} else if(field.equals("IM")){
		    field3 = value;
		    newInst = mipsOpcode + " "+field1 + ", " + field2 + ", " + field3+"\n";
		} else if(field.equals("IM")&&opcode.equals("beq")){
		    continue;
		}
		
		mipsList.set(instructionLine, newInst);
		    }
		    if(copyBit!=1){
		BufferedWriter bw = null;
		FileWriter fw = null;

			
			fw = new FileWriter(filename);
			bw = new BufferedWriter(fw);
			
			bw.write(dumps.useless);
			for(int j=0;j<mipsList.size();j++){
			bw.write(mipsList.get(j));
			}
			bw.close();
		    }
			}
		}
		}
		}
		return "";
	}
	
	public void insecure(int lineNumber, int count, int maxAddress, int modify, ArrayList<String> list1) throws IOException{
	    Dumps dumps = new Dumps();
	    int copyBit = 0;
	    MipsRegisters mips = new MipsRegisters();
	    ArrayList<String>mipsList = new ArrayList<String>();
		mipsList = dumps.readMips(lineNumber);
		Random rand = new Random();
	    for(int z=0;z<modify;z++){
		int n = rand.nextInt(list1.size() * 32);
		double m = Math.floor(n / 32);
		int instructionLine = (int) m;
		int bit = n - (instructionLine * 32);
		String elements[] = list1.get(instructionLine).trim().split("");
		if (elements[bit].equals("0")) {
			elements[bit] = "1";
		} else {
			elements[bit] = "0";
		}
		String inst = "";
		for(int k = 0 ; k<elements.length;k++){
			inst = inst + elements[k];
		}
	    String output = dumps.fieldSwitch(inst, bit);
	    if(!output.equals("none")){
	    String filename = "insecure_"+count;
	    if(output.equals("US")){
		 File file = new File(filename);
		 copyBit = 1;
		    dumps.copyFiles(file); 
		    break;
		} 
		if(!output.equals("US")){
	    
		String mipsInst = mipsList.get(instructionLine);
		if(mipsInst.contains(":")){
		    mipsInst = mipsList.get(instructionLine+1);
		}
		
		String mipsOpcode = "";
		String fieldsArray[] = null;
		String field1="";
		String field2 = "";
		String field3="";
		String offset = "";
		if(!mipsInst.contains(".")){
		if(!mipsInst.contains("jal")){
		    mipsInst = mipsInst.trim();
		mipsOpcode = mipsInst.substring(0,mipsInst.indexOf("$"));
		
		    mipsOpcode = mipsOpcode.trim();
		    mipsInst = mipsInst.replace(mipsOpcode, "");
		    fieldsArray = mipsInst.split(",");
		    if(fieldsArray.length==2){
			if(mipsOpcode.equals("lw")||mipsOpcode.equals("sw")){
			    field1 = fieldsArray[0].trim();
			    offset = mipsInst.substring(mipsInst.indexOf("(")-1, mipsInst.indexOf("("));
			    field2 = mipsInst.substring(mipsInst.indexOf("(")+1, mipsInst.indexOf(")"));
			} else if(mipsOpcode.equals("li")){
			    field1 = fieldsArray[0].trim();
				field2 = fieldsArray[1].trim(); 
			}
			else {
			    field1 = fieldsArray[0].trim();
				field2 = fieldsArray[1].trim();
			}
		    } else if(fieldsArray.length==3){
			field1 = fieldsArray[0].trim();
			field2 = fieldsArray[1].trim();
			field3 = fieldsArray[2].trim();
		    } 
		} else {
		    mipsOpcode = mipsInst.substring(0,5);
		    mipsOpcode = mipsOpcode.trim();
		    mipsInst = mipsInst.replace(mipsOpcode, "");
		    mipsInst = mipsInst.trim();
		    field1= mipsInst;
		}
		    String newInst = "";
		    String newOp = "";
		    int newReg = 0;
		    String newRegister="";
		if(output.equals("opcode")){
		    if(fieldsArray==null){
			 newOp = shuffleKeys("opcode");
			    newOp = mips.getOpcode(newOp);
			    newInst = newOp + " " + field1+"\n";
			    mipsList.set(instructionLine, newInst);
		    } 
		    if(fieldsArray!=null){
		    if(fieldsArray.length==2){
			if(mipsOpcode.equals("lw")||mipsOpcode.equals("sw")||mipsOpcode.equals("li")){
			    newOp = shuffleKeys("opcode");
			    newOp = mips.getOpcode(newOp);
			    newInst = newOp + " " + field1 + ", " + offset + "("+field2+")\n";
			    mipsList.set(instructionLine, newInst);
			} else {
			    newOp = shuffleKeys("opcode");
			    newOp = mips.getOpcode(newOp);
			    newInst = newOp + " " + field1 + ", " +field2+"\n";
			    mipsList.set(instructionLine, newInst);
			}
		    } else if(fieldsArray.length==3){
			newOp = shuffleKeys("opcode");
			newOp = mips.getOpcode(newOp);
			
			 newInst = newOp + " " + field1 + ", " +field2+", " + field3 + "\n";
		    }
		    }
		} else if(output.equals("R")){
		   if(fieldsArray!=null){
		    if(fieldsArray.length==1){
			newOp = shuffleKeys("R");
			    newRegister = mips.getRegisters(Integer.parseInt(newOp));
			    newInst = mipsOpcode + " " + newRegister +"\n";
			    mipsList.set(instructionLine, newInst);
		    }
		    if(fieldsArray.length==2){
			if(mipsOpcode.equals("lw")||mipsOpcode.equals("sw")||mipsOpcode.equals("li")){
			    newOp = shuffleKeys("R");
			    newRegister = mips.getRegisters(Integer.parseInt(newOp));
			    newInst = mipsOpcode + " " + newRegister + ", " + offset + "("+field2+")\n";
			    mipsList.set(instructionLine, newInst);
			} else {
			    newOp = shuffleKeys("R");
			    newRegister = mips.getRegisters(Integer.parseInt(newOp));
			    newInst = mipsOpcode + " " + field1 + ", " +newRegister+"\n";
			    mipsList.set(instructionLine, newInst);
			}
		    } else if(fieldsArray.length==3){
			newOp = shuffleKeys("R");
			newRegister = mips.getRegisters(Integer.parseInt(newOp));
			ArrayList<Integer> intArray = new ArrayList<Integer>();
			intArray.add(1);
			intArray.add(2);
			intArray.add(3);
			Collections.shuffle(intArray);
			if(intArray.get(0)==1){
			 newInst = mipsOpcode + " " + newRegister + ", " +field2+", " + field3 + "\n";
			} else if (intArray.get(0)==2){
			    newInst = mipsOpcode + " " + field1 + ", " +newRegister+", " + field3 + "\n";
			} else if(intArray.get(0)==3){
			    newInst = mipsOpcode + " " + field1 + ", " +field2+", " + newRegister + "\n";
			}
		    }
		   }
		} else if(output.equals("M")){
		    int randomNum = rand.nextInt((maxAddress - 1) + 1) + 1;
		    newInst = mipsOpcode + " " + field1  + ", " + field2 +", " + randomNum;
		}
		
		
			mipsList.set(instructionLine, newInst);
		}
		if(copyBit!=1){
		BufferedWriter bw = null;
		FileWriter fw = null;
			
			fw = new FileWriter("insecure_"+count);
			bw = new BufferedWriter(fw);
			
			bw.write(dumps.useless);
			for(int j=0;j<mipsList.size();j++){
			bw.write(mipsList.get(j));
			}
			bw.close();
			}
	    }
		}
		
	    }
	}
		
	
	public String fieldSwitch(String instruction, int bit){
	    /*String instArray[] = instruction.split("!");
	    instruction = instArray[0];*/
	    //int bit = Integer.parseInt(instArray[1]);
	    String opcodeBinary = instruction.substring(0,6);
	    if(opcodeBinary.equals("000000")){
		opcodeBinary = instruction.substring(27,31);
	    }
	    MipsRegisters mips = new MipsRegisters();
	    Dumps dumps = new Dumps();
	    String opcode = mips.getOpcode(opcodeBinary);
	    if(opcode==null){
		return "none";
	    }
	    if(opcode.equals("add")||opcode.equals("sub")||opcode.equals("addu")||opcode.equals("subu")||opcode.equals("sltu")||opcode.equals("slt")){
	    if(bit>=0&&bit<6){
		return "opcode";
	    }
		else if(bit>=6&&bit<=11){
		return "R";
		} else if(bit>=11&&bit<=16){
		    return "R";
		} else if(bit>=16&&bit<=21){
		    return "R";
		} else {
		int address = dumps.binDec(instruction.substring(21,32));
		return "US";
		}
		//return resultRegister3 + "!" + register1 + "!" + register2 + "!" + address;
	    } else if(opcode.equals("addi")||opcode.equals("addiu")){
		if(bit>=0&&bit<6){
		    return "opcode";
		    }
			else if(bit>=6&&bit<=11){
			    return "R";
		} else if(bit>=11&&bit<=16){
		    return "R";
		} else if(bit>=16&&bit<=32){
		    return "M";
		} 
		//return opcode + "!" + resultRegister3 + "!" + register1 + "!" + immediate;
	    } else if(opcode.equals("mult")||opcode.equals("multu")||opcode.equals("div")||opcode.equals("divu")){
		if(bit>=0&&bit<6){
			return "opcode";
		    }
			else if(bit>=6&&bit<=11){
			    return "R";
			} else if(bit>=11&&bit<=16){
			    return "R";
			} else if(bit>=16&&bit<=21){
			    return "R";
			} else {
			return "US";
			}
		//return opcode + "!" + resultRegister3 + "!" + register1 + "!" + register2 + "!" + address;
	    } else if(opcode.equals("and")||opcode.equals("or")||opcode.equals("xor")||opcode.equals("nor")||opcode.equals("sllv")||opcode.equals("srlv")||opcode.equals("srav")){
		if(bit>=0&&bit<6){
		    return "opcode";
		    }
			else if(bit>=6&&bit<=11){
			    return "R";
			} else if(bit>=11&&bit<=16){
			    return "R";
			} else if(bit>=16&&bit<=21){
			    return "R";
			} else {
			    return "US";
			}
		//return opcode + "!" + resultRegister3 + "!" + register1 + "!" + register2 + "!" + address;
	    } else if(opcode.equals("andi")||opcode.equals("ori")||opcode.equals("xori")){
		if(bit>=0&&bit<6){
		    return "opcode";
		    }
			else if(bit>=6&&bit<=11){
			    return "R";
			} else if(bit>=11&&bit<=16){
			    return "R";
			} else if(bit>=16&&bit<=32){
			    return "M";
			}
		//return opcode + "!" + resultRegister3 + "!" + register1 + "!" + immediate;
	    } else if (opcode.equals("sw") || opcode.equals("lw") || opcode.equals("sh") || opcode.equals("sb")
		|| opcode.equals("lh") || opcode.equals("lhu") || opcode.equals("lb") || opcode.equals("lbu")) {
		if(bit>=0&&bit<6){
			return "opcode";
		    }
			else if(bit>=6&&bit<=11){
			    return "R";
	    	} else if(bit>=11&&bit<=16){
	    	return "R";
	    	} else if(bit>16){
	    	    return "US";
	    	}
		//return opcode + "!" + register1 + "!" + register2;
	    } else if(opcode.equals("beq")||opcode.equals("bne")||opcode.equals("slti")||opcode.equals("sltiu")){
		if(bit>=0&&bit<6){
			return "opcode";
		    }
			else if(bit>=6&&bit<=11){
			    return "R";
			} else if(bit>=11&&bit<=16){
			    return "R";
			}  else if(bit>16 && bit<32) {
			    return "M";
			}
		//return opcode + "!" + resultRegister3 + "!" + register1 + "!" + immediate;
	    } else if(opcode.equals("jal")||opcode.equals("j")||opcode.equals("jr")){
		if(bit>=0&&bit<6){
			return "opcode";
		    }
			else if(bit>=16&&bit<=32){
		return "US";
		} else if(bit>=6&&bit<=31){
		    return "US";
		}
		//return opcode + "!" + address;
	    } else if(opcode.equals("li")){
		return "US";
	    } else if(opcode==null){
		return "none";
	    }
	    return "none1";
	}
	public String checkFormat(String instruction) throws IOException{
	    String instArray[] = instruction.split("!");
	    instruction = instArray[0];
	    int bit = Integer.parseInt(instArray[1]);
	    String opcodeBinary = instruction.substring(0,6);
	    if(opcodeBinary.equals("000000")){
		opcodeBinary = instruction.substring(27,31);
	    }
	    MipsRegisters mips = new MipsRegisters();
	    Dumps dumps = new Dumps();
	    String opcode = mips.getOpcode(opcodeBinary);
	    if(opcode==null){
		return "none";
	    }
	    if(opcode.equals("add")||opcode.equals("sub")||opcode.equals("addu")||opcode.equals("subu")||opcode.equals("sltu")||opcode.equals("slt")){
	    if(bit>=0&&bit<6){
		return opcode + "!opcode!" + "N";
	    }
		else if(bit>=6&&bit<=11){
		String resultRegister3 = mips.getRegisters(dumps.binDec(instruction.substring(6,11)));
		return opcode+"!RR!"+resultRegister3;
		} else if(bit>11&&bit<=16){
		String register1 = mips.getRegisters(dumps.binDec(instruction.substring(11,16)));
		return opcode+"!R1!"+register1;
		} else if(bit>16&&bit<=21){
		String register2 = mips.getRegisters(dumps.binDec(instruction.substring(16,21)));
		return opcode+"!R2!"+register2;
		} else {
		int address = dumps.binDec(instruction.substring(21,32));
		return opcode+"!AD!"+address;
		}
		//return resultRegister3 + "!" + register1 + "!" + register2 + "!" + address;
	    } else if(opcode.equals("addi")||opcode.equals("addiu")){
		if(bit>=0&&bit<6){
			return opcode + "!opcode!" + "N";
		    }
			else if(bit>=6&&bit<=11){
		String resultRegister3 = mips.getRegisters(dumps.binDec(instruction.substring(6,11)));
		return opcode+"!RR!"+resultRegister3;
		} else if(bit>11&&bit<=16){
		String register1 = mips.getRegisters(dumps.binDec(instruction.substring(11,16)));
		return opcode+"!R1!"+register1;
		} else if(bit>16&&bit<=32){
		int immediate = dumps.binDec(instruction.substring(16,32));
		return opcode+"!IM!"+immediate;
		} /*if(bit>=0&&bit<6){
		    return opcode + "!US!US";
		}*/
		//return opcode + "!" + resultRegister3 + "!" + register1 + "!" + immediate;
	    } else if(opcode.equals("mult")||opcode.equals("multu")||opcode.equals("div")||opcode.equals("divu")){
		if(bit>=0&&bit<6){
			return opcode + "!opcode!" + "N";
		    }
			else if(bit>=6&&bit<=11){
			String resultRegister3 = mips.getRegisters(dumps.binDec(instruction.substring(6,11)));
			return opcode+"!RR!"+resultRegister3;
			} else if(bit>11&&bit<=16){
			String register1 = mips.getRegisters(dumps.binDec(instruction.substring(11,16)));
			return opcode+"!R1!"+register1;
			} else if(bit>16&&bit<=21){
			String register2 = mips.getRegisters(dumps.binDec(instruction.substring(16,21)));
			return opcode+"!R2!"+register2;
			} else {
			int address = dumps.binDec(instruction.substring(21,32));
			return opcode+"!AD!"+address;
			}
		//return opcode + "!" + resultRegister3 + "!" + register1 + "!" + register2 + "!" + address;
	    } else if(opcode.equals("and")||opcode.equals("or")||opcode.equals("xor")||opcode.equals("nor")||opcode.equals("sllv")||opcode.equals("srlv")||opcode.equals("srav")){
		if(bit>=0&&bit<6){
			return opcode + "!opcode!" + "N";
		    }
			else if(bit>=6&&bit<=11){
			String resultRegister3 = mips.getRegisters(dumps.binDec(instruction.substring(6,11)));
			return opcode+"!RR!"+resultRegister3;
			} else if(bit>11&&bit<=16){
			String register1 = mips.getRegisters(dumps.binDec(instruction.substring(11,16)));
			return opcode+"!R1!"+register1;
			} else if(bit>16&&bit<=21){
			String register2 = mips.getRegisters(dumps.binDec(instruction.substring(16,21)));
			return opcode+"!R2!"+register2;
			} else {
			int address = dumps.binDec(instruction.substring(21,32));
			return opcode+"!AD!"+address;
			}
		//return opcode + "!" + resultRegister3 + "!" + register1 + "!" + register2 + "!" + address;
	    } else if(opcode.equals("andi")||opcode.equals("ori")||opcode.equals("xori")){
		if(bit>=0&&bit<6){
			return opcode + "!opcode!" + "N";
		    }
			else if(bit>=6&&bit<=11){
			String resultRegister3 = mips.getRegisters(dumps.binDec(instruction.substring(6,11)));
			return opcode+"!RR!"+resultRegister3;
			} else if(bit>11&&bit<=16){
			String register1 = mips.getRegisters(dumps.binDec(instruction.substring(11,16)));
			return opcode+"!R1!"+register1;
			} else if(bit>16&&bit<=32){
			int immediate = dumps.binDec(instruction.substring(16,32));
			return opcode+"!IM!"+immediate;
			}
		//return opcode + "!" + resultRegister3 + "!" + register1 + "!" + immediate;
	    } else if (opcode.equals("sw") || opcode.equals("lw") || opcode.equals("sh") || opcode.equals("sb")
		|| opcode.equals("lh") || opcode.equals("lhu") || opcode.equals("lb") || opcode.equals("lbu")) {
		if(bit>=0&&bit<6){
			return opcode + "!opcode!" + "N";
		    }
			else if(bit>=6&&bit<=11){
		String register1 =  mips.getRegisters(dumps.binDec(instruction.substring(6,11)));
		return opcode + "!R1!"+register1;
	    	} else if(bit>11&&bit<=16){
		String register2 = mips.getRegisters(dumps.binDec(instruction.substring(11,16)));
		return opcode+"!R2!"+register2;
	    	} else if(bit>16){
	    	    return opcode + "!US!US";
	    	}
		//return opcode + "!" + register1 + "!" + register2;
	    } else if(opcode.equals("beq")||opcode.equals("bne")||opcode.equals("slti")||opcode.equals("sltiu")){
		if(bit>=0&&bit<6){
			return opcode + "!opcode!" + "N";
		    }
			else if(bit>=6&&bit<=11){
			String resultRegister3 = mips.getRegisters(dumps.binDec(instruction.substring(6,11)));
			return opcode+"!RR!"+resultRegister3;
			} else if(bit>11&&bit<=16){
			String register1 = mips.getRegisters(dumps.binDec(instruction.substring(11,16)));
			return opcode+"!R1!"+register1;
			}  else if(bit>16 && bit<32) {
		String immediate = instruction.substring(16,32);
		return opcode+"!IM!"+immediate;
			}
		//return opcode + "!" + resultRegister3 + "!" + register1 + "!" + immediate;
	    } else if(opcode.equals("jal")||opcode.equals("j")||opcode.equals("jr")){
		if(bit>=0&&bit<6){
			return opcode + "!opcode!" + "N";
		} else if(bit>=6&&bit<=16){
		    return opcode+"!US!US";
		} else if(bit>16&&bit<=31){
		int address = dumps.binDec(instruction.substring(16,31));
		return opcode+"!AD!"+address;
		} 
	    } else if(opcode.equals("li")){
		return opcode + "!US!US";
	    } 
	    return "none1";
	}
	
	public ArrayList<String> readMips(int lineNumber) throws IOException{
	    useless = "";
	    FileReader in = new FileReader("fact.asm");
		BufferedReader br = new BufferedReader(in);
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> list1 = new ArrayList<String>();
		list.clear();
		list1.clear();
			String line;
			int ct = 0;
			
			while ((line = br.readLine()) != null) {
				list.add(line + "\n");

			}
			for(int i=0;i<list.size();i++){
			    if(i<=41){
				useless = useless + "\n" + list.get(i);
			    }
			    if(i>41){
				list1.add(list.get(i));
			    }
			}
			return list1;
	}
	
	
	public Integer binDec(String binary){
	    int decimal = Integer.parseInt(binary,2);
	    return decimal;
	}
	
	public File copyFiles(File filename) throws IOException{
	    File file1 = new File("fact.asm");
	    FileChannel src = new FileInputStream(file1).getChannel();
	    FileChannel dest = new FileOutputStream(filename).getChannel();
	    dest.transferFrom(src, 0, src.size());
	    return filename;
	}
	
	public String shuffleKeys(String values){
	    MipsRegisters mips = new MipsRegisters();
	    List<String> instList = new ArrayList<String>();
	    List<Integer> regList = new ArrayList<Integer>();
	    if(values.equals("opcode")){
		instList = mips.returnInst();
		Collections.shuffle(instList);
		return instList.get(0);
	    } else {
		regList = mips.returnReg();
		Collections.shuffle(regList);
		return regList.get(0).toString();
	    }
	}
	
	public void executeUI() throws IOException{
	    FileReader in = new FileReader("newDump");
		BufferedReader br = new BufferedReader(in);
		Dumps dumps  = new Dumps();
		Random rand = new Random();
		double rate = rand.nextInt((10 - 2) + 1) + 2;
		rate = rate/10;
		DecimalFormat f = new DecimalFormat("##.#######");
		System.out.println("Unreliability rate in PPM::" + f.format(rate/1000000) + "%");
		ArrayList<String> totalList = new ArrayList<String>();
		ArrayList<String> list1 = new ArrayList<String>();
		ArrayList<String> instructionList = new ArrayList<String>();
		int lineNumber = 0;
		
			String line;
			int ct=0;
			while ((line = br.readLine()) != null) {
			    ct++;
			    if(ct>1){
			    totalList.add(line + "\n");
			    }
			}
			br.close();
			for(int x = 0;x<totalList.size();x++){
			    String opcode = totalList.get(x).substring(0,6);
			    String address = totalList.get(x).substring(totalList.get(x).length()-7,totalList.get(x).length());
			    if(opcode.equals("000011")){
				lineNumber = dumps.binDec(address.trim());
				lineNumber = lineNumber+ 1;
				break;
			    }
			    
			}
			
			for(int o=0;o<totalList.size();o++){
			   
			    if(o>=lineNumber){
				list1.add(totalList.get(o));
			    }
			}
			for(int i=0;i<100;i++){
			//Random rand = new Random();
			int n = rand.nextInt(list1.size() * 32);
			double m = Math.floor(n / 32);
			int instructionLine = (int) m;
			int bit = n - (instructionLine * 32);
			String filename = "unreliableInsecure_"+i;
			String elements[] = list1.get(instructionLine).trim().split("");
			if (elements[bit].equals("0")) {
				elements[bit] = "1";
			} else {
				elements[bit] = "0";
			}
			String inst = "";
			for(int k = 0 ; k<elements.length;k++){
				inst = inst + elements[k];
			}
			ArrayList<String>mipsList = new ArrayList<String>();
			mipsList = dumps.readMips(lineNumber);
			int maxAddress = list1.size() + lineNumber;
			//String res = dumps.unreliable(instructionLine, bit, inst, lineNumber, i);
			    String output = dumps.checkFormat(inst + "!" + bit);
				if(output.equals("none")){
				    //return "exit";
				} else {
				String[] outputArray = output.split("!");
				String opcode = outputArray[0];
				String field = outputArray[1];
				String value = outputArray[2];
				
				/*if(field.equals("US")){
				 File file = new File(filename);
				    dumps.copyFiles(file);   
				}*/ 
				if(!field.equals("US")||!value.equals("US")){
				
				String mipsInst = mipsList.get(instructionLine);
				if(mipsInst.contains(":")){
				    mipsInst = mipsList.get(instructionLine+1);
				}
				String mipsOpcode = "";
				String fieldsArray[] = null;
				String field1="";
				String field2 = "";
				String field3="";
				String offset = "";
				
				if(!mipsInst.contains("jal")||!mipsInst.contains("j")){
				    if(!mipsInst.substring(0,1).equals(".")){
				    mipsOpcode = mipsInst.substring(0,mipsInst.indexOf("$"));
				    mipsOpcode = mipsOpcode.trim();
				    mipsInst = mipsInst.replace(mipsOpcode, "");
				    fieldsArray = mipsInst.split(",");
				    if(fieldsArray.length==2){
					if(mipsOpcode.equals("lw")||mipsOpcode.equals("sw")){
					    field1 = fieldsArray[0].trim();
					    offset = mipsInst.substring(mipsInst.indexOf("(")-1, mipsInst.indexOf("("));
					    field2 = mipsInst.substring(mipsInst.indexOf("(")+1, mipsInst.indexOf(")"));
					} else if(mipsOpcode.equals("li")){
					    field1 = fieldsArray[0].trim();
					    field2 = fieldsArray[1].trim();
					} else {
					    field1 = fieldsArray[0].trim();
						field2 = fieldsArray[1].trim();
					}
				    } else if(fieldsArray.length==3){
					field1 = fieldsArray[0].trim();
					field2 = fieldsArray[1].trim();
					field3 = fieldsArray[2].trim();
				    
				    
				} else {
				    mipsOpcode = mipsInst.substring(0,3);
				    mipsOpcode = mipsOpcode.trim();
				    mipsInst = mipsInst.replace(mipsOpcode, "");
				    fieldsArray[0] = mipsInst;
				    field1 = mipsInst;
				}
				String newInst = "";
				if(!(opcode.equals(mipsOpcode))){
				    if(fieldsArray.length==1){
					newInst = opcode + " " + field1;
				    }
				    if(field3.length()==0||field3.equals("N")){
				    newInst = opcode + " " +field1 + ", " +offset+"("+ field2+")\n";
				    } else {
					newInst = opcode + " " +field1 + ", " + field2 + ", " + field3+"\n"; 	
				    }
				} else if(field.equals("RR")){
				    field1 = value;
				    newInst = mipsOpcode + " " +field1 + ", " + field2 + ", " + field3+"\n";
				} else if(field.equals("R1")){
				    if(opcode.equals("lw")||opcode.equals("sw")||opcode.equals("li")){
					newInst = mipsOpcode + " " +field1 + ", " +offset+"("+ field2+")\n";
				    } else {
				    field2 = value;
				    newInst = mipsOpcode + " " +field1 + ", " + field2 + ", " + field3+"\n";
				    }
				} else if(field.equals("R2")){
				    if(opcode.equals("lw")||opcode.equals("sw")||opcode.equals("li")){
					newInst = mipsOpcode + " " +field1 + ", " +offset+"("+ field2+")\n";
				    } else {
				    field3 = value;
				    newInst = mipsOpcode + " "+field1 + ", " + field2 + ", " + field3+"\n";
				    }
				 }  else if(opcode.equals("sw") && field.equals("R2")){
				    field2 = value;
				    newInst = mipsOpcode + " "+field1 + ", " + offset+"("+field2+")"+"\n";
				} else if(field.equals("IM")){
				    field3 = value;
				    newInst = mipsOpcode + " "+field1 + ", " + field2 + ", " + field3+"\n";
				} else if(field.equals("IM")&&opcode.equals("beq")){
				    //return "exit";
				}
				
				mipsList.set(instructionLine, newInst);
					}
				}
				}
				}

			//dumps.insecure(instructionLine, bit, inst, lineNumber, i, maxAddress);
			    MipsRegisters mips = new MipsRegisters();
			    output = dumps.fieldSwitch(inst, bit);
			    if(!output.equals("none")){
			    if(output.equals("US")){
				 File file = new File(filename);
				    dumps.copyFiles(file);   
				} 
				if(!output.equals("US")){
				String mipsInst = mipsList.get(instructionLine);
				if(mipsInst.contains(":")){
				    mipsInst = mipsList.get(instructionLine+1);
				}
				
				String mipsOpcode = "";
				String fieldsArray[] = null;
				String field1="";
				String field2 = "";
				String field3="";
				String offset = "";
				if(!mipsInst.contains(".")){
				if(!mipsInst.contains("jal")){
				    mipsInst = mipsInst.trim();
				mipsOpcode = mipsInst.substring(0,mipsInst.indexOf("$"));
				
				    mipsOpcode = mipsOpcode.trim();
				    mipsInst = mipsInst.replace(mipsOpcode, "");
				    fieldsArray = mipsInst.split(",");
				    if(fieldsArray.length==2){
					if(mipsOpcode.equals("lw")||mipsOpcode.equals("sw")){
					    field1 = fieldsArray[0].trim();
					    offset = mipsInst.substring(mipsInst.indexOf("(")-1, mipsInst.indexOf("("));
					    field2 = mipsInst.substring(mipsInst.indexOf("(")+1, mipsInst.indexOf(")"));
					} else if(mipsOpcode.equals("li")){
					    field1 = fieldsArray[0].trim();
						field2 = fieldsArray[1].trim(); 
					}
					else {
					    field1 = fieldsArray[0].trim();
						field2 = fieldsArray[1].trim();
					}
				    } else if(fieldsArray.length==3){
					field1 = fieldsArray[0].trim();
					field2 = fieldsArray[1].trim();
					field3 = fieldsArray[2].trim();
				    } 
				} else {
				    mipsOpcode = mipsInst.substring(0,5);
				    mipsOpcode = mipsOpcode.trim();
				    mipsInst = mipsInst.replace(mipsOpcode, "");
				    mipsInst = mipsInst.trim();
				    field1= mipsInst;
				}
				    String newInst = "";
				    String newOp = "";
				    int newReg = 0;
				    String newRegister="";
				if(output.equals("opcode")){
				    if(fieldsArray==null){
					 newOp = shuffleKeys("opcode");
					    newOp = mips.getOpcode(newOp);
					    newInst = newOp + " " + field1+"\n";
					    mipsList.set(instructionLine, newInst);
				    } 
				    if(fieldsArray!=null){
				    if(fieldsArray.length==2){
					if(mipsOpcode.equals("lw")||mipsOpcode.equals("sw")||mipsOpcode.equals("li")){
					    newOp = shuffleKeys("opcode");
					    newOp = mips.getOpcode(newOp);
					    newInst = newOp + " " + field1 + ", " + offset + "("+field2+")\n";
					    mipsList.set(instructionLine, newInst);
					} else {
					    newOp = shuffleKeys("opcode");
					    newOp = mips.getOpcode(newOp);
					    newInst = newOp + " " + field1 + ", " +field2+"\n";
					    mipsList.set(instructionLine, newInst);
					}
				    } else if(fieldsArray.length==3){
					newOp = shuffleKeys("opcode");
					newOp = mips.getOpcode(newOp);
					
					 newInst = newOp + " " + field1 + ", " +field2+", " + field3 + "\n";
				    }
				    }
				} else if(output.equals("R")){
				   if(fieldsArray!=null){
				    if(fieldsArray.length==1){
					newOp = shuffleKeys("R");
					    newRegister = mips.getRegisters(Integer.parseInt(newOp));
					    newInst = mipsOpcode + " " + newRegister +"\n";
					    mipsList.set(instructionLine, newInst);
				    }
				    if(fieldsArray.length==2){
					if(mipsOpcode.equals("lw")||mipsOpcode.equals("sw")||mipsOpcode.equals("li")){
					    newOp = shuffleKeys("R");
					    newRegister = mips.getRegisters(Integer.parseInt(newOp));
					    newInst = mipsOpcode + " " + newRegister + ", " + offset + "("+field2+")\n";
					    mipsList.set(instructionLine, newInst);
					} else {
					    newOp = shuffleKeys("R");
					    newRegister = mips.getRegisters(Integer.parseInt(newOp));
					    newInst = mipsOpcode + " " + field1 + ", " +newRegister+"\n";
					    mipsList.set(instructionLine, newInst);
					}
				    } else if(fieldsArray.length==3){
					newOp = shuffleKeys("R");
					newRegister = mips.getRegisters(Integer.parseInt(newOp));
					ArrayList<Integer> intArray = new ArrayList<Integer>();
					intArray.add(1);
					intArray.add(2);
					intArray.add(3);
					Collections.shuffle(intArray);
					if(intArray.get(0)==1){
					 newInst = mipsOpcode + " " + newRegister + ", " +field2+", " + field3 + "\n";
					} else if (intArray.get(0)==2){
					    newInst = mipsOpcode + " " + field1 + ", " +newRegister+", " + field3 + "\n";
					} else if(intArray.get(0)==3){
					    newInst = mipsOpcode + " " + field1 + ", " +field2+", " + newRegister + "\n";
					}
				    }
				   }
				} else if(output.equals("M")){
				    int randomNum = rand.nextInt((maxAddress - 1) + 1) + 1;
				    newInst = mipsOpcode + " " + field1  + ", " + field2 +", " + randomNum;
				}
				
				BufferedWriter bw = null;
				FileWriter fw = null;
					//mipsList.set(instructionLine, newInst);
					
					fw = new FileWriter(filename);
					bw = new BufferedWriter(fw);
					
					bw.write(dumps.useless);
					for(int j=0;j<mipsList.size();j++){
					bw.write(mipsList.get(j));
					}
					bw.close();
					}
				}
				
			    }
			}
			
			}
		
	}
	
	

