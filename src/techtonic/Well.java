/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package techtonic;

/**
 *
 * @author 1412625
 */
public class Well implements  Comparable<Well>{
    private int id;
    private String operator;
    public Well(int id, String op){
        this.id = id;
        this.operator = op;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public int compareTo(Well o) {
        return (this.operator).compareTo(o.getOperator());
    }
    @Override
    public String toString(){
     return getOperator();
    }
}
