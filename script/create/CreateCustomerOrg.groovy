import org.moqui.context.ExecutionContext
import java.sql.Timestamp

ExecutionContext ec=context.ec;

    def serviceResult = ec.service.sync()
            .name("PartyServices.find#CustomerOrg")
            .parameters([emailAddress: email]).call()
    if(serviceResult.partyIdList){
        return ec.message.addError("Email Already Exists")
    }

    // Get the current timeStamp
    Timestamp date = new Timestamp(System.currentTimeMillis())

    // Create Party
    def newParty = ec.entity.makeValue("party.Party")
    newParty.setSequencedIdPrimary()
            .set("partyTypeEnumId", "Person")
            .create()
    def partyId = newParty.partyId

    // Create Organization
    def person = ec.entity.makeValue("party.Organization")
    person.set("partyId", partyId)
    person.set("organizationName", organizationName)
    person.set("officeSiteName", officeSiteName)
    person.set("annualRevenue", annualRevenue)
    person.set("numEmployees", numEmployees)
    person.create()

    //Create ContactMech
    def contactMech = ec.entity.makeValue("party.contact.ContactMech")
    contactMech.setSequencedIdPrimary()
            .set("contactMechTypeEnumId", "CmtEmailAddress")
    contactMech.set("infoString", email).create()

    //Create PartyContactMech
    def partyContactMech = ec.entity.makeValue("party.contact.PartyContactMech")
    partyContactMech.set("partyId", partyId)
    partyContactMech.set("contactMechId",contactMech.contactMechId)
    partyContactMech.set("contactMechPurposeId", "EmailPrimary")
    partyContactMech.set("fromDate",date )
    partyContactMech.create()

    return [partyId: partyId, message: "Customer created successfully"]