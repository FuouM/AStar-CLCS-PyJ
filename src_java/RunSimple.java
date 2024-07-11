import datastructures.CLCSInstance;

public class RunSimple {
    public static void main(String[] args) {
        // String raw_p_con = "cbb";
        // String raw_s0 = "bcaacbdba";
        // String raw_s1 = "cbccadcbbd";
        
        // String raw_p_con = "ddddcacbcbcbbaadccddadbcbccbcbdcdccdbaccdabcdaacdb";
        // String raw_s0 = "dbdddbacbdccacbdbcbbccbdbcaacbdacdacdbddadbcacacbbbcdaabbccbbccbdbcdccbdabdaaacccdcaabbcbdcaaccbddbc";
        // String raw_s1 = "adddbdddcacbaaabcabcbbcadaababddccddcaddbcaddbccbcbccbdddcdadcdbccddbcacacdcddccadbcdccccbdcadacdcdb";
        

        // Cant get generated sols
        // String raw_p_con = "aabdbdccddddcdbbcbbdaddbddabcbaccacbabacabcdbbaacb";
        // String raw_s0 = "adabcabbddbbdccddddcdadccddbaabccbbbbaadadddacbbbcdbacdaddabbacbadccaccbbadbcddacadbdbcddbbbacaccabd";
        // String raw_s1 = "aaaaadbdbdcccadcdbdddcbaadddbcbccbbddadddbbaadadbabcabddacbacddaaccbabbabaacaaabbbcbaadccbbcbbaabdcb";
        
        // usually give less than 10
        String raw_p_con = "HKH";
        String raw_s0 = "MVVDLPRYLPLLLLLELWEPMYLLCSQPKGLSRAHWFEIQHVQTSRQPCNTAMRGVNNYTQHCKQINTFLHESFQNVAATCSLHNITCKNGRKNCHESAEPVKMTDCSHTGGAYPNCRYSSDKQYKFFIVACEHPKKEDPPYQLVPVHLDKIV";
        String raw_s1 = "MKPLVIKFAWPLPLLLLLLLPPKLQGNYWDFGEYELNPEVRDFIREYESTGPTKPPTVKRIIEMITIGDQPFNDYDYCNTELRTKQIHYKGRCYPEHYIAGVPYGELVKACDGEEVQCKNGVKSCRRSMNLIEGVRCVLETGQQMTNCTYKTILMIGYPVVSCQWDEETKIFIPDHIYNMSLPK";
        
        // Freezes if ease out the cond
        // String raw_p_con = "bcdcadcccdbbdddbcccaaccabaaccabbcbcababbacddcacdddbbabdbacdbcadacbdbbcdabadbdabaaacdcccdbbddbabcabbbcacbacabdabdccaabdcbaacaacacdcdacbcccdaabcccddbadbbaddadbabdcdbdbcdccbaccacabacbbdaadbaabcaababdaabbcbbdcacacdbbcaaddbdddacbadbddbbdaddcaaabcccbbaddba";
        // String raw_s0 = "bbbdaacaccccbaddabbdbbcdcadbbcdcbccacadbbbdcdbbbdddaabbbacbcbcaacdccaaaddcccccabadbadacadbbccdcacaabcadccabdbdbcddcacabbbbbadbdcdbdccadababbdbaddacaabaaddccbbabacaaddbabbbcbacdcacaabbdabdcaadcdbadccbbbbaddaabbdddddbaacadbcdbcbdacdbaadbbbaccbabcabbcadddcccabcccdabdcdbdccddbbcdbdcdaddaccdbadadccacabaadbcbcabacdddadbabaadbaadcaaaaabdcdaacabccdbddcdcbaaccdcacabdabcbbadcdaabbabcdadadddccdabaaabcccabbdbcbacdabaacdcbdcabcdbacccaabdcdcbadbadbabaccadaaadacaadbdabddcddccaaaaacbbbbacbdbcbaabbacbbaacbababddbbcdccabaddcccdcdddcdcddbcdcbadcdabaabbbabcbcadcadbcaccdcaadabbbbbbcacccbddbddcdaddaddbbcbadadcaaabbccbabcacbadcddccbbbaadbdcdacbbcbcdabbaabcbbdacdddcdcccdacdbacadabdcbccbdcdbcbcddbabddcdcbaccdabacbaddccacbabacccdccbbbcbdadaaadaabbadccccbbdadbababbdbccabbccacdaabcabddadaccbdabbabcbbbabdaacbdbcdbaddcddbcbcccacdcaddbcacacacddbdadbcdbcadaabbabccdaadaabaadbdcdbddadacaddbdccdddadddacdccabadddbaadddbadbcdbbdcbdbbdadbabaadbabbdddadaddddcadcbaddaabcaacbbdcccbbcbadcbcdcbadbbaaddbbcddddabdccdabcdcaaaaddcc";
        // String raw_s1 = "bdcccadbadcccadcdcbdccbdcdabdabbdcddbcdacbbddbcbccdacdaabdbdbbddcdcadacaccbaaacbaccdacbbcddabdacddccbcddaddaccdadbaabbdacbcdbabdacadbdddbaaacdabbacabbbcadbccaacaadcddacacaaacadddadabddbcaddbaacaadbbbdbaaadadcabacbcaccdabbacadcacbcdbcbbdbabcdbcabbadcabdcdabbdddacbccbbbcbbdccbdaaacbcaabaadccadcccbddcbbdcdbbbdabcdbbddbccdadbababbcdbabdbaabaaabdddbcacaabdbacddbcaadbcbadaacacdccabacbbadccbaabdabbddbcababadccbadcbaaaadacbabdbcadabddcbcbbcdacabaaaadabadcadbbbdacabccccabcdbccdabdddbcadddcdacabbbcdcbaccacaddbacbbdabbaaaabbdcbcbcadcbcaabcccdcdcbcccbbcadbddcbbcbdcbddcbcdcdaadddbbbadddbacbbdabdabbabbaddaaadccdcbdcacbdcdabaccbbcddbbadabccabbdacccdcdbdadbaacbccaccbacbbdccacaadccccdbaccddadcbccbaadbacacadaacadcabcaacabacaccbaaaddccabccaaddaddcabbcbdbbcbdaaadbdaccbaadbbbccbabdadcacadcdbcaadaabdcaccbddcbaabdcdabdadcadabbabdbcaccddbdbbdbdcbcdcadbccaaadaccbdadbcabcbbdcbdcabccbadcdabdbadbdcdbcbbadbaaabdaabdcccbbaddadcabcdcbcaddaadbdbdcdbdcacbbccccdbabddbabbdddadccbcabddaadbabaddacaacbaabbccacdbabcaddbbdba";
        
        // String raw_p_con = "bcdcadcccdbbdddbcccaaccabaaccabbcbcababbacddcacdddbbabdbacdbcadacbdbbcdabadbdabaaacdcccdbbddbabcabbbcacbacabdabdccaabdcbaacaacacdcdacbcccdaabcccddbadbbaddadbabdcdbdbcdccbaccacabacbbdaadbaabcaababdaabbcbbdcacacdbbcaaddbdddacbadbddbbdaddcaaabcccbbaddbabbdaacacccbaddabbbbdcbbdcbcacadbbbdbaabbbacbcaacdaddcccbaddcadbbcdcacaabcadcdbdcddacabbbbadbdcdbdcadbabbdaddacabaaddccabaababbbcbacdacabbdabdaacbaccbbddaabddddaadbcbcbdacdbaadbbacbabcbbcaddcccbcccdabdddccdbcddddaccdadccacabaadcbcabacddadbabadadcaaaab";
        // String raw_s0 = "dcbdacabccdbdcdaddccccdbabbacddcadadbabccbccaaccabaaadcccabbcbaacbababbacbdadbccadcdadddabbabdddbccdaaacadbcacdcdbacacdabbacdcbdabcddcbbccacadabdcbdabdaddbabdbaabacaacadaacadacccadcbdadddbbddcbabcdaababbcaaccbbabbbcabdacbbaaabcbbbabdbbdccaadbbbddccbaacacbacaadcdcdccddcdacbdcdcccdacaddbbcdcccdbdabdadbbcaadbdaadbabdcbdbbdbbacdbcadcbcaaccdacbacabacbbcadaadbdbbbbabaaccbbcaddbddabdaabddadbaabbbccdbbdacacaaabcccabaccdbcbbcaaadcdccdbbbdbddadabcdacbbcccdabbabacdbbabddbbcdddadddccdaacacbccaccdbbbaaddbacadbbaccdacabcdadcccbbadcbcdddbabdacbbbbdbdcbbdaccdabcbcacaadddbbccdcbcbcbbdaadadabababbbabacccbccaaccbddadbdabdcbccbbdcbabdcdcccdaacadddaccbbcbdcdaabbcbacbaabbdacadacdbdcdbddcdadaddcbcbabcbccdbcaddbadbcbdcdbdcacdbacababdddabddaacdcdbacadbaaddaccabaabababbabbccbadcadacadabaabdbbddabdacdbdaacbaacacddcbdbdccddaabdddaddcddcabaaddadbcdbaadbdddcbcbbdbdaccdbaabddbbbaacbbaabbaacdbabcaddcbcdddcabdcccdadabddddcccdbdbcdddaabcdcdbaccdcdacdcbcabbacabdcbdcaadcbacdbadabbbcdaddcdadbdadccdacdbacabaadaaddcdaaaccab";
        // String raw_s1 = "bcdcdcacabdcccdbbaddddcbccccdcbaadccbdcaabdabcdbcdaaacbbdcdcabcbbcbbcccabaabbacddccdaabdabdcdbdddcdaddbabacbabadbabcdbacadaccbdbbdaccbbcddddabacdddcbcdbcddddaabddaaaacdcacbdcdabdbbdcddbadadbcaddbabaabccdbbaacacbabcabbdbcaabdcdcaabdbcccabaaaacaacacacddcaacdaaaacdbdaabdccadcdacdaabcccaacdddbbadbbadbdadaabaabdcddbddcbacbacbadccccabbaacccbcacbdcababacbbbcbdcabcdabdadadbdaabcbccacbababbdaabbcbbbdccbacdcaccbddbbbcacaaadbdaaadbcdcddacbdbcdcabbdbddbbbabdacdbdbdcaaddbabccccbbcdaadbaabddbabbdaacbcadbbdbcccabaaaddaababbdbddbbdccaadbbbdcabccddabcaacdadcabdaacdbccababbdcbdabbdaaabbdbbbcabaacbcbacaadbcdadaadacdacccbbdabcdaddcaabddbbbccbbdcdcacacaaabacadadabacddbaddbbbcddcbdaccccabbbbcbdabdbcddacdbdcbdaddbcadddababbbdaddbacdcbcabacaaaadddbadbcbbcababaaaababbbbdccbbcbcacaddabccababdabcabcdcaacddcbbaccccbbdbbcaddaabddbdddadcabbdbcbdcbcbcdbdcdabcccdaadbaadddbbbbacbddbaabccbbdbbacbaddcccbcdcababdaacaddacbcdddcbdccacbdcdabccdbccddbbcddbdadccdaabccabdacccccdacabdabaaadacccbacccabbacdadadcbabbadabdcaaacacab";
        
        // String raw_p_con = "fygxmpbbrq";
        // String raw_s0 = "fsoyigixsmpdybhbrqmp";
        // String raw_s1 = "dfvywgjwsxmpxbfbrlkq";
        

        CLCSInstance clcsInstance = StringsSolver.createInstanceFromStrings(raw_p_con, raw_s0, raw_s1);
        String deoResult = StringsSolver.DEO_Solver(clcsInstance);
        String astarResult = StringsSolver.AStar_Solver(clcsInstance);

        int deoLength = deoResult.length();
        int astarLength = astarResult.length();

        System.out.println("[DP_DEO](" + deoLength + ") " + deoResult);
        System.out.println("[A_STAR](" + astarLength + ") " + astarResult);
        System.out.println("Match str: " + deoResult.equals(astarResult));
        System.out.println("Match len: " + (deoLength == astarLength));

        System.out.println(clcsInstance.getIndexToChar());
    }
}
