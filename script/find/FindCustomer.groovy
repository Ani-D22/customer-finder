import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityFind
import org.moqui.entity.EntityList
import org.moqui.entity.EntityValue

//==============================================================================

ExecutionContext ec=context.ec
EntityFind ef=ec.entity.find("party.FindCustomerView").distinct(true);

    ef.selectField("partyId")

    if(emailAddress) {ef.condition("emailAddress",emailAddress)}
    if(firstName) {ef.condition("firstName",firstName)}
    if(lastName) {ef.condition("lastName",lastName)}
    if(contactNumber) {ef.condition("contactNumber",contactNumber)}
    if(postalAddress) {ef.condition("postalAddress",postalAddress)}

//==============================================================================

    if (orderByField) {
        if (orderByField.contains("fullName")) {
            ef.orderBy("firstName,middleName,lastName")
        } else {
            ef.orderBy(orderByField)
        }
    }

    if (!pageNoLimit) {
        ef.offset(pageIndex as int, pageSize as int);
        ef.limit(pageSize as int)
    }

    partyIdList = []
    EntityList entityList = ef.list()
    entityList.each { EntityValue ev ->
        partyIdList.add(ev.partyId)
    }

    partyIdListCount = ef.count()
    partyIdListPageIndex = ef.pageIndex
    partyIdListPageSize = ef.pageSize

//==============================================================================