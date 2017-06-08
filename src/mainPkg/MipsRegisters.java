
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MipsRegisters {

	static HashMap<Integer,String> RegisterMap = new HashMap<Integer,String>();
	static HashMap<String,String> InstructionMap = new HashMap<String,String>();
	
	public String getRegisters(int key) throws IOException{
		
		RegisterMap.put(0, "$zero");
		RegisterMap.put(1, "$at");
		RegisterMap.put(2, "$v0");
		RegisterMap.put(3, "$v1");
		RegisterMap.put(4, "$a0");
		RegisterMap.put(5, "$a1");
		RegisterMap.put(6, "$a2");
		RegisterMap.put(7, "$a3");
		RegisterMap.put(8, "$t0");
		RegisterMap.put(9, "$t1");
		RegisterMap.put(10, "$t2");
		RegisterMap.put(11, "$t3");
		RegisterMap.put(12, "$t4");
		RegisterMap.put(13, "$t5");
		RegisterMap.put(14, "$t6");
		RegisterMap.put(15, "$t7");
		RegisterMap.put(16, "$s0");
		RegisterMap.put(17, "$s1");
		RegisterMap.put(18, "$s2");
		RegisterMap.put(19, "$s3");
		RegisterMap.put(20, "$s4");
		RegisterMap.put(21, "$s5");
		RegisterMap.put(22, "$s6");
		RegisterMap.put(23, "$s7");
		RegisterMap.put(24, "$t8");
		RegisterMap.put(25, "$t9");
		RegisterMap.put(26, "$k0");
		RegisterMap.put(27, "$k1");
		RegisterMap.put(28, "$gp");
		RegisterMap.put(29, "$sp");
		RegisterMap.put(30, "$fp");
		RegisterMap.put(31, "$ra");
		return RegisterMap.get(key);
	}
	public String getOpcode(String key) {
	   
		InstructionMap.put("100000", "add");
		InstructionMap.put("100010", "sub");
		InstructionMap.put("001000", "addi");
		InstructionMap.put("100001", "addu");
		InstructionMap.put("100011", "subu");
		InstructionMap.put("001001", "addiu");
		InstructionMap.put("011000", "mult");
		InstructionMap.put("011001", "multu");
		InstructionMap.put("011010", "div");
		InstructionMap.put("011011", "divu");
		//InstructionMap.put("010000", "mfhi");
		//InstructionMap.put("010010", "mflo");
		InstructionMap.put("100100", "and");
		InstructionMap.put("100101", "or");
		InstructionMap.put("100110", "xor");
		InstructionMap.put("100111", "nor");
		InstructionMap.put("001100", "andi");
		InstructionMap.put("001101", "ori");
		InstructionMap.put("001110", "xori");
		//InstructionMap.put("000000", "sll");
		InstructionMap.put("000010", "srl");
		InstructionMap.put("000011", "sra");
		InstructionMap.put("000110", "srlv");
		InstructionMap.put("000111", "srav");
		InstructionMap.put("101011", "sw");
		InstructionMap.put("101001", "sh");
		InstructionMap.put("101000", "sb");
		InstructionMap.put("100011", "lw");
		InstructionMap.put("100001", "lh");
		InstructionMap.put("100101", "lhu");
		InstructionMap.put("100000", "lb");
		InstructionMap.put("100100", "lbu");
		InstructionMap.put("001111", "lui"); 
		InstructionMap.put("000100", "beq"); 
		InstructionMap.put("000101", "bne"); 
		InstructionMap.put("101010", "slt"); 
		InstructionMap.put("001010", "slti"); 
		InstructionMap.put("101001", "sltu"); 
		InstructionMap.put("001001", "sltiu"); 
		InstructionMap.put("000010", "j"); 
		InstructionMap.put("01000", "jr"); 
		InstructionMap.put("000011", "jal"); 
		InstructionMap.put("001001", "li");
		return InstructionMap.get(key);
		
           
} 
	public List<String> returnInst(){
	    HashMap<String,String> InstructionMa = new HashMap<String,String>();
	    InstructionMa.put("100000", "add");
		InstructionMa.put("100010", "sub");
		InstructionMa.put("001000", "addi");
		InstructionMa.put("100001", "addu");
		InstructionMa.put("100011", "subu");
		InstructionMa.put("001001", "addiu");
		InstructionMa.put("011000", "mult");
		InstructionMa.put("011001", "multu");
		InstructionMa.put("011010", "div");
		InstructionMa.put("011011", "divu");
		//InstructionMa.put("010000", "mfhi");
		//InstructionMa.put("010010", "mflo");
		InstructionMa.put("100100", "and");
		InstructionMa.put("100101", "or");
		InstructionMa.put("100110", "xor");
		InstructionMa.put("100111", "nor");
		InstructionMa.put("001100", "andi");
		InstructionMa.put("001101", "ori");
		InstructionMa.put("001110", "xori");
		//InstructionMap.put("000000", "sll");
		InstructionMa.put("000010", "srl");
		InstructionMa.put("000011", "sra");
		InstructionMa.put("000110", "srlv");
		InstructionMa.put("000111", "srav");
		InstructionMa.put("101011", "sw");
		InstructionMa.put("101001", "sh");
		InstructionMa.put("101000", "sb");
		InstructionMa.put("100011", "lw");
		InstructionMa.put("100001", "lh");
		InstructionMa.put("100101", "lhu");
		InstructionMa.put("100000", "lb");
		InstructionMa.put("100100", "lbu");
		InstructionMa.put("001111", "lui"); 
		InstructionMa.put("000100", "beq"); 
		InstructionMa.put("000101", "bne"); 
		InstructionMa.put("101010", "slt"); 
		InstructionMa.put("001010", "slti"); 
		InstructionMa.put("101001", "sltu"); 
		InstructionMa.put("001001", "sltiu"); 
		InstructionMa.put("000010", "j"); 
		InstructionMa.put("01000", "jr"); 
		InstructionMa.put("000011", "jal"); 
		InstructionMa.put("001001", "li");

	    List<String> list = new ArrayList<String>(InstructionMap.keySet());
	    return list;
	}
	
	public List<Integer> returnReg(){
	    HashMap<Integer,String> RegisterMa = new HashMap<Integer,String>();
	    RegisterMa.put(0, "$zero");
		RegisterMa.put(1, "$at");
		RegisterMa.put(2, "$v0");
		RegisterMa.put(3, "$v1");
		RegisterMa.put(4, "$a0");
		RegisterMa.put(5, "$a1");
		RegisterMa.put(6, "$a2");
		RegisterMa.put(7, "$a3");
		RegisterMa.put(8, "$t0");
		RegisterMa.put(9, "$t1");
		RegisterMa.put(10, "$t2");
		RegisterMa.put(11, "$t3");
		RegisterMa.put(12, "$t4");
		RegisterMa.put(13, "$t5");
		RegisterMa.put(14, "$t6");
		RegisterMa.put(15, "$t7");
		RegisterMa.put(16, "$s0");
		RegisterMa.put(17, "$s1");
		RegisterMa.put(18, "$s2");
		RegisterMa.put(19, "$s3");
		RegisterMa.put(20, "$s4");
		RegisterMa.put(21, "$s5");
		RegisterMa.put(22, "$s6");
		RegisterMa.put(23, "$s7");
		RegisterMa.put(24, "$t8");
		RegisterMa.put(25, "$t9");
		RegisterMa.put(26, "$k0");
		RegisterMa.put(27, "$k1");
		RegisterMa.put(28, "$gp");
		RegisterMa.put(29, "$sp");
		RegisterMa.put(30, "$fp");
		RegisterMa.put(31, "$ra");
	
	    List<Integer> list = new ArrayList<Integer>(RegisterMa.keySet());
	    return list;
	}
		
		
	}
	

