import sqldb.ChocanConnection;

/**
 * Created by bspriggs on 11/10/2016.
 */
public class Main extends Utilities{
    public static void main(String[] args) throws Exception{

        ChocanConnection obj = new ChocanConnection();
        System.out.println("Hello world!");
        Menu menu = new Menu();


        String userType;
        System.out.print("Please enter if you are a provider, operator, manager: (enter 'quit' to exit)");
        userType = input.next();

        while(userType != "quit") {
            if ( userType == "provider" )
                menu.providerMenu();
            else if ( userType == "operator" )
                menu.operatorMenu();
            else if ( userType == "manager" )
                menu.managerMenu();
        }



        /*
        Reports T = new Reports();

        WriteToDisk m_report = new WriteToDisk();

        m_report.WriteOutMember(T.WriteMemberReport(123456789),123456789);
        m_report.WriteOutProviders(T.WriteProviderReport(987654321),987654321);
        T.SummarizeReports(false);
        T.PrintMemberReport(123456789);
        T.PrintProviderReport(987654321);
        T.SummarizeReports(true);



        obj.getProviderReport();
        */
    }


}
