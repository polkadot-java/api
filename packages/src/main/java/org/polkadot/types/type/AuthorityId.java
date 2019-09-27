package org.polkadot.types.type;


import org.polkadot.types.primitive.generic.AccountId;

// Wrapper for a AuthorityId. Same as an normal AccountId, i.e. a wrapper
// around publicKey, however specialized since it specifically points to
// an authority.
public class AuthorityId extends AccountId {
    public AuthorityId(Object value) {
        super(value);
    }
}
