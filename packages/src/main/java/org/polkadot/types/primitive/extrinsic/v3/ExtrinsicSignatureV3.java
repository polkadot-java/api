package org.polkadot.types.primitive.extrinsic.v3;


import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.types.Types;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.U8;
import org.polkadot.types.primitive.extrinsic.ExtrinsicEra;
import org.polkadot.types.primitive.extrinsic.Types.ExtrinsicSignatureOptions;
import org.polkadot.types.primitive.extrinsic.v2.ExtrinsicSignatureV2;
import org.polkadot.types.primitive.generic.Address;
import org.polkadot.types.rpc.RuntimeVersion;
import org.polkadot.types.type.NonceCompact;
import org.polkadot.types.type.Signature;
import org.polkadot.types.type.SignaturePayload;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @name ExtrinsicSignatureV3
 * @description A container for the [[Signature]] associated with a specific [[Extrinsic]]
 */
public class ExtrinsicSignatureV3 extends ExtrinsicSignatureV2 {

    public static final byte[] IMMORTAL_ERA = new byte[]{0};
    public static final int BIT_SIGNED = 0b10000000;
    public static final int BIT_UNSIGNED = 0;
    public static final int BIT_VERSION = 0b0000001;

    public ExtrinsicSignatureV3(Object value, ExtrinsicSignatureOptions options) {
        super(value, options);
    }


    private ExtrinsicSignatureV3 injectSignature(Signature signature, Address signer, NonceCompact nonce, ExtrinsicEra era) {
        this.put("era", era);
        this.put("nonce", nonce);
        this.put("signer", signer);
        this.put("signature", signature);
        this.put("version", new U8(BIT_VERSION | BIT_SIGNED));
        return this;
    }


    /**
     * Adds a raw signature
     */
    //addSignature (_signer: Address | Uint8Array, _signature: Uint8Array, _nonce: AnyNumber, _era: Uint8Array = IMMORTAL_ERA): ExtrinsicSignature {
    ExtrinsicSignatureV3 addSignature(Object _signer, byte[] _signature, Object _nonce, byte[] _era) {
        Address signer = new Address(_signer);
        NonceCompact nonce = new NonceCompact(_nonce);
        ExtrinsicEra era = new ExtrinsicEra(_era);
        Signature signature = new Signature(_signature);
        return this.injectSignature(signature, signer, nonce, era);
    }


    /**
     * Generate a payload and pplies the signature from a keypair
     */
    //sign (method: Method, account: KeyringPair, { blockHash, era, nonce, version }: SignatureOptions): ExtrinsicSignature {
    ExtrinsicSignatureV3 sign(Method method, KeyringPair account, Types.SignatureOptions signatureOptions) {


        Address signer = new Address(account.publicKey());

        Map<String, Object> values = new LinkedHashMap<>();
        values.put("nonce", signatureOptions.getNonce());
        values.put("method", method);
        values.put("era", signatureOptions.getEra() == null ? IMMORTAL_ERA : signatureOptions.getEra());
        values.put("blockHash", signatureOptions.getBlockHash());

        SignaturePayload signingPayload = new SignaturePayload(values);

        Signature signature = new Signature(signingPayload.sign(account, (RuntimeVersion) signatureOptions.getVersion()));

        return this.injectSignature(signature, signer, signingPayload.getNonce(), signingPayload.getEra());
    }

}
