import org.moqui.context.ExecutionContext
import java.sql.Timestamp

ExecutionContext ec=context.ec;

    def serviceResult = ec.service.sync()
            .name("PartyServices.find#CustomerPerson")
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

    // Create Person
    def person = ec.entity.makeValue("party.Person")
    person.set("partyId", partyId)
    person.set("firstName", firstName)
    person.set("middleName", middleName)
    person.set("lastName", lastName)
    person.set("birthDate", birthDate)
    person.set("occupation", occupation)
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