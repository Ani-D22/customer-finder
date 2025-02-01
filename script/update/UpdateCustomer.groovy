import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityFind
import org.moqui.entity.EntityValue
import java.sql.Timestamp

ExecutionContext ec = context.ec
Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis())

EntityFind entityFind = ec.entity
        .find('party.FindCustomerView' as Closure) as EntityFind
entityFind.selectFields()

    if (emailAddress) {
        entityFind.condition("emailAddress", emailAddress)
    }
    EntityValue entityValue = entityFind.one()

    if (entityValue != null) {
        EntityValue existingPartyContactMech = ec.entity
                .find('party.contact.PartyContactMech' as Closure)
                .condition("contactMechId", el.contactMechId)
                .condition("contactMechPurposeId", "EmailPrimary")
                .one()

        if (existingPartyContactMech != null) {
            existingPartyContactMech.set('thruDate', currentTimestamp)
            existingPartyContactMech.update()
        }

        def newContactMech = ec.entity.makeValue('party.contact.ContactMech')
        newContactMech.setFields(context, true, null, null)
        newContactMech.setSequencedIdPrimary()
        newContactMech.contactMechTypeEnumId = "CmtEmailAddress"
        newContactMech.infoString = emailAddress
        newContactMech.create()

        def partyId = el.partyId

        def newPartyContactMech = ec.entity.makeValue('party.contact.PartyContactMech')
        newPartyContactMech.set('partyId', partyId)
        newPartyContactMech.set('contactMechId', newContactMech.contactMechId)
        newPartyContactMech.set('contactMechPurposeId', 'EmailPrimary')
        newPartyContactMech.set('fromDate', currentTimestamp)
        newPartyContactMech.create()

        if (postalAddress) {
            def postalAddressEntity = ec.entity.makeValue('party.contact.PostalAddress')
            postalAddressEntity.set('contactMechId', newContactMech.contactMechId)
            postalAddressEntity.set('address', address)
            postalAddressEntity.set('city', city)
            postalAddressEntity.set('postalCode', postalCode)

            postalAddressEntity.create()
        }
        if (phoneNumber) {
            def contactNumberEntity = ec.entity.makeValue('party.contact.ContactNumber')
            contactNumberEntity.set('contactMechId', newContactMech.contactMechId)
            contactNumberEntity.set('contactNumber', phoneNumber)
            contactNumberEntity.create()
        }

        }
        else {
            throw new RuntimeException("Customer not found with email: ${emailAddress}")
        }

        return [partyId: partyId,message: "Customer record updated successfully."]