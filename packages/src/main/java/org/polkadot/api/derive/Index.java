package org.polkadot.api.derive;

import com.google.common.collect.Maps;
import org.polkadot.api.Types.ApiInterfacePromise;
import org.polkadot.api.derive.accounts.AccountFunctions;
import org.polkadot.api.derive.balances.BalancesFunctions;
import org.polkadot.api.derive.chain.ChainFunctions;
import org.polkadot.api.derive.democracy.DemocracyFunctions;
import org.polkadot.api.derive.session.SessionFunctions;
import org.polkadot.api.derive.staking.StakingFunctions;
import org.polkadot.direct.IFunction;
import org.polkadot.direct.IModule;
import org.polkadot.direct.ISection;

import java.util.Map;
import java.util.Set;

public class Index {

    public static DeriveCustom functions = new DeriveCustom();

    static {
        functions.addSection("accounts", new DeriveCustomSection());
        functions.section("accounts").addFunction("idAndIndex", AccountFunctions::idAndIndex);
        functions.section("accounts").addFunction("idToIndex", AccountFunctions::idToIndex);
        functions.section("accounts").addFunction("indexToId", AccountFunctions::indexToId);
        functions.section("accounts").addFunction("indexes", AccountFunctions::indexes);

        functions.addSection("balances", new DeriveCustomSection());
        functions.section("balances").addFunction("fees", BalancesFunctions::fees);
        functions.section("balances").addFunction("validatingBalance", BalancesFunctions::validatingBalance);
        functions.section("balances").addFunction("validatingBalances", BalancesFunctions::validatingBalances);
        functions.section("balances").addFunction("votingBalance", BalancesFunctions::votingBalance);
        functions.section("balances").addFunction("votingBalances", BalancesFunctions::votingBalances);
        functions.section("balances").addFunction("votingBalancesNominatorsFor", BalancesFunctions::votingBalancesNominatorsFor);

        functions.addSection("chain", new DeriveCustomSection());
        functions.section("chain").addFunction("bestNumber", ChainFunctions::bestNumber);
        functions.section("chain").addFunction("bestNumberFinalized", ChainFunctions::bestNumberFinalized);
        functions.section("chain").addFunction("bestNumberLag", ChainFunctions::bestNumberLag);
        functions.section("chain").addFunction("getHeader", ChainFunctions::getHeader);
        functions.section("chain").addFunction("subscribeNewHead", ChainFunctions::subscribeNewHead);

        functions.addSection("democracy", new DeriveCustomSection());
        functions.section("democracy").addFunction("referendumInfos", DemocracyFunctions::referendumInfos);
        functions.section("democracy").addFunction("referendums", DemocracyFunctions::referendums);
        functions.section("democracy").addFunction("referendumVotesFor", DemocracyFunctions::referendumVotesFor);
        functions.section("democracy").addFunction("votes", DemocracyFunctions::votes);

        functions.addSection("session", new DeriveCustomSection());
        functions.section("session").addFunction("eraLength", SessionFunctions::eraLength);
        functions.section("session").addFunction("eraProgress", SessionFunctions::eraProgress);
        functions.section("session").addFunction("sessionProgress", SessionFunctions::sessionProgress);

        functions.addSection("staking", new DeriveCustomSection());
        functions.section("staking").addFunction("controllers", StakingFunctions::controllers);
        functions.section("staking").addFunction("intentionsBalances", StakingFunctions::intentionsBalances);
    }

    interface DeriveCustomMethod extends IFunction {
        Types.DeriveRealFunction apply(ApiInterfacePromise api);
    }


    public static class DeriveCustomSection extends ISection<DeriveCustomMethod> {

    }

    public static class DeriveCustom implements IModule<DeriveCustomSection> {
        Map<String, DeriveCustomSection> sectionMap = Maps.newLinkedHashMap();

        @Override
        public DeriveCustomSection section(String section) {
            return sectionMap.get(section);
        }

        @Override
        public Set<String> sectionNames() {
            return sectionMap.keySet();
        }

        @Override
        public void addSection(String sectionName, DeriveCustomSection section) {
            this.sectionMap.put(sectionName, section);
        }
    }


    public static class DeriveRealSection extends ISection<Types.DeriveRealFunction> {
    }

    public static class DeriveReal implements IModule<DeriveRealSection> {
        Map<String, DeriveRealSection> sectionMap = Maps.newLinkedHashMap();

        @Override
        public DeriveRealSection section(String section) {
            return sectionMap.get(section);
        }

        @Override
        public Set<String> sectionNames() {
            return sectionMap.keySet();
        }

        @Override
        public void addSection(String sectionName, DeriveRealSection section) {
            this.sectionMap.put(sectionName, section);
        }
    }

    public static class Derive extends DeriveReal {

        DeriveRealSection account;
        DeriveRealSection balances;
        DeriveRealSection chain;
        DeriveRealSection democracy;
        DeriveRealSection session;
        DeriveRealSection staking;

        //accounts: ReturnTypes<typeof accounts>;
        //balances: ReturnTypes<typeof balances>;
        //chain: ReturnTypes<typeof chain>;
        //democracy: ReturnTypes<typeof democracy>;
        //session: ReturnTypes<typeof session>;
        //staking: ReturnTypes<typeof staking>;


        public Derive() {
            this.addSection("accounts", new DeriveRealSection());
            this.addSection("balances", new DeriveRealSection());
            this.addSection("chain", new DeriveRealSection());
            this.addSection("democracy", new DeriveRealSection());
            this.addSection("session", new DeriveRealSection());
            this.addSection("staking", new DeriveRealSection());

            account = this.section("account");
            balances = this.section("balances");
            chain = this.section("chain");
            democracy = this.section("democracy");
            session = this.section("session");
            staking = this.section("staking");
        }

    }


    static Derive injectFunctions(ApiInterfacePromise api, Derive derive, DeriveCustom functions) {
        if (functions == null) {
            return derive;
        }
        for (String sectionName : functions.sectionNames()) {
            DeriveCustomSection section = functions.section(sectionName);
            DeriveRealSection result = derive.section(sectionName);

            for (String methodName : section.functionNames()) {
                result.addFunction(methodName, section.function(methodName).apply(api));
            }
        }
        return derive;
    }


    public static Derive decorateDerive(ApiInterfacePromise api, DeriveCustom custom) {
        Derive derive = new Derive();

        injectFunctions(api, derive, functions);
        injectFunctions(api, derive, custom);

        return derive;
    }
}
