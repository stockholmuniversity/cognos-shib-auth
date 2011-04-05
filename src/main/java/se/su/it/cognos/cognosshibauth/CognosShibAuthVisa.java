package se.su.it.cognos.cognosshibauth;

import com.cognos.CAM_AAA.authentication.*;

/**
 * Created by IntelliJ IDEA.
 * User: joakim
 * Date: 2011-04-05
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */

public class CognosShibAuthVisa implements IVisa {

    public ITrustedCredential generateTrustedCredential(IBiBusHeader iBiBusHeader) throws UserRecoverableException, SystemRecoverableException, UnrecoverableException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ICredential generateCredential(IBiBusHeader iBiBusHeader) throws UserRecoverableException, SystemRecoverableException, UnrecoverableException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isValid() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IAccount getAccount() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IGroup[] getGroups() {
        return new IGroup[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IRole[] getRoles() {
        return new IRole[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
