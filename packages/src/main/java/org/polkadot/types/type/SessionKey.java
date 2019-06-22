package org.polkadot.types.type;

/**
 * Wrapper for a SessionKey. Same as an normal AuthorityId, i.e. a wrapper
 * around publicKey.
 */
public class SessionKey extends AuthorityId {
    public SessionKey(Object value) {
        super(value);
    }
}
