/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package techtonic;

import org.jwitsml.WitsmlWellbore;

/**
 *
 * @author 1412625
 */
public class Wellbore {
    private int id;
    private WitsmlWellbore wellbore;

    public Wellbore(int id, WitsmlWellbore wellbore) {
        this.id = id;
        this.wellbore = wellbore;
    }

    public int getId() {
        return id;
    }

    public WitsmlWellbore getWellbore() {
        return wellbore;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWellbore(WitsmlWellbore wellbore) {
        this.wellbore = wellbore;
    }
    
}
