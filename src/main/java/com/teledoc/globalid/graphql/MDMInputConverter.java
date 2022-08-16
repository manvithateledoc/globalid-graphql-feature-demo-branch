package com.teledoc.globalid.graphql;

import com.teledoc.globalid.graphql.entities.*;
import lombok.extern.slf4j.Slf4j;
import madison.mpi.*;
import madison.util.SetterException;

import java.time.LocalDate;

@Slf4j
public class MDMInputConverter {

    private MemHead getMemHead(MemRowList memberRows){

        MemHead memHead;

        if(memberRows.rowAt(0) != null && memberRows.rowAt(0).getMemHead() != null){
            memHead = memberRows.rowAt(0).getMemHead();
        }else if(memberRows.rowAt(0) instanceof MemHead){
            memHead = (MemHead)memberRows.rowAt(0);
        }else{
            memHead = new MemHead();
            memberRows.addRow(memHead);
        }

        return memHead;
    }

    public MemRowList convert(AddressInput... addressInput) throws SetterException {

        MemRowList memberRows = new MemRowList();

        return this.convert(memberRows, addressInput);
    }

    public MemRowList convert(MemRowList memberRows, AddressInput... addressInput) throws SetterException {

        var memHead = this.getMemHead(memberRows);

        if(addressInput != null) {
            for (AddressInput input : addressInput) {


                var memAttr = new MemAttrRow("TMEMADDR");
                memAttr.setMemRecno(memHead.getMemRecno());

                String attrCode = "";

                switch (input.getUse()) {
                    case OLD: {
                        attrCode = "HOMEADDRESS";
                        break;
                    }
                    case HOME: {
                        attrCode = "HOMEADDRESS";
                        break;
                    }
                    case TEMP: {
                        // todo fix correct address
                        attrCode = "MAILADDRESS";
                        break;
                    }
                    case WORK: {
                        attrCode = "WORKADDRESS";
                        break;
                    }
                    case BILLING: {attrCode = "BILLADDRESS";
                        break;
                    }
                    default: {
                        //todo fix correct attr code
                        attrCode = "HOMEADDRESS";
                        break;
                    }
                }


                memAttr.setAttrCode(attrCode);

                memAttr.setString("CITY", input.getCity());
                memAttr.setString("STATE", input.getState());
                memAttr.setString("COUNTRY", input.getCountry());
                memAttr.setString("ZIPCODE", input.getPostalCode());


                int counter = 1;
                for (String line : input.getLine()) {
                    switch (counter) {
                        case 1: {
                            memAttr.setString("STLINE1", line);

                            break;
                        }
                        case 2: {
                            memAttr.setString("STLINE2", line);
                            break;
                        }
                        case 3: {
                            memAttr.setString("STLINE3", line);
                            break;
                        }
                        case 4: {
                            memAttr.setString("STLINE4", line);
                            break;
                        }

                    }

                    ++counter;
                }


                memberRows.addRow(memAttr);


            }
        }

        return memberRows;

    }

    public MemRowList convert( NameInput... nameInput) throws SetterException {
        return this.convert(new MemRowList(), nameInput);
    }

    public MemRowList convert(MemRowList memberRows, NameInput... nameInput) throws SetterException {

        MemHead memHead = this.getMemHead(memberRows);


        if(nameInput != null) {
            for (NameInput input : nameInput) {

                if(input.getGiven() != null){


                           MemAttrRow attrRow = new MemAttrRow("TMEMNAME");
                           attrRow.setMemRecno(memHead.getMemRecno());
                        attrRow.setMemHead(memHead);
                       attrRow.setAttrCode("USUALNAME");
                        attrRow.setAsString("family", input.getFamily());
                        attrRow.setAsString("given", input.getGiven()[0]);
                        memberRows.addRow(attrRow);



                }

            }
        }

        return memberRows;
    }

    public MemRowList convert( ContactPointInput... contactPointInput) throws SetterException {

        return this.convert(new MemRowList(), contactPointInput);

    }

    public MemRowList convert(MemRowList memberRows , ContactPointInput... contactPointInput) throws SetterException {

        MemHead memHead = this.getMemHead(memberRows);

        if(contactPointInput != null) {
            for (ContactPointInput input : contactPointInput) {

                switch (input.getSystem()) {
                    case PHONE: {

                        var phone = new MemAttrRow("TMEMTELECOM");
                        phone.setMemRecno(memHead.getMemRecno());

                        switch (input.getUse()) {
                            case HOME: {
                                phone.setAttrCode("HOMEPHONE");
                                break;
                            }
                            case WORK:
                            {
                                phone.setAttrCode("WORKPHONE");
                                break;                            }

                            case MOBILE: {
                                phone.setAttrCode("MOBLPHONE");
                                break;

                            }
                            default: {
                                phone.setAttrCode("UNSPECPHONE");

                            }
                        }

                        phone.setString("COMVAL", input.getValue());

                        memberRows.addRow(phone);

                        break;
                    }
                    case EMAIL: {
                        var email = new MemAttrRow("TMEMTELECOM");
                        email.setMemRecno(memHead.getMemRecno());

                        email.setAttrCode("HOMEEMAIL");
                        email.setString("COMVAL", input.getValue());
                        memberRows.addRow(email);

                        break;
                    }

                }


            }
        }

        return memberRows;
    }

    public MemRowList convert(GenderIdentity identity){
        return this.convert(new MemRowList(), identity);
    }
    public MemRowList convert(MemRowList memberRows, GenderIdentity identity){

        if(identity == null){
            identity = GenderIdentity.NON_DISCLOSE;
        }

        MemHead memHead = this.getMemHead(memberRows);


        MemRow maIds = new MemRow("MemAttr");

        try {
            //init row with MemRecno/EntRecno of memHead
            maIds.init(memHead);
            maIds.setString("AttrCode", "GENDER");
            switch(identity) {
                case MALE: {
                    maIds.setString("AttrVal", "M");
                    break;
                }
                case FEMALE: {
                    maIds.setString("AttrVal", "F");
                    break;
                }
                case NON_DISCLOSE: {
                    maIds.setString("AttrVal", "U");
                    break;
                }
                default : {
                    maIds.setString("AttrVal", "O");
                    break;
                }
            }

            memberRows.addRow(maIds);

            return memberRows;

        } catch (SetterException e) {
            log.error("Error occurred while add data to memRow {}", e.getMessage());
            throw new IllegalArgumentException(e);
        }

    }

    public MemRowList convert( AdministrativeGender gender){
        return this.convert(new MemRowList(), gender);
    }
    public MemRowList convert(MemRowList memberRows, AdministrativeGender gender){

        MemHead memHead = this.getMemHead(memberRows);

        if(gender == null){
            gender = AdministrativeGender.UNKNOWN;
        }

        MemAttr maIds = new MemAttr(memHead);

        //init row with MemRecno/EntRecno of memHead
        maIds.setAttrCode("GENDER");
        switch(gender) {
            case MALE: {
                maIds.setAttrVal("M");
                break;
            }
            case FEMALE: {
                maIds.setAttrVal("F");
                break;
            }
            case OTHER: {
                maIds.setAttrVal("O");
                break;
            }
            case UNKNOWN: {
                maIds.setAttrVal("U");
                break;
            }
        }

        memberRows.addRow(maIds);

        return memberRows;



    }

    public MemRowList convert(ConsumerInput... consumerInput) throws SetterException {

        MemRowList memberRows  = new MemRowList();

        MemHead memHead = new MemHead();
        memHead.setMemRecno(0);
        memHead.setEntRecno(0);

        memberRows.addRow(memHead);

        return this.convert(memberRows, consumerInput);
    }

    public MemRowList convert(MemRowList memberRows, ConsumerInput... consumerInput) throws SetterException {

        for(ConsumerInput input : consumerInput){

            memberRows = this.convert(memberRows, input.getAddresses());
            memberRows = this.convert(memberRows, input.getContactPoints());
            memberRows = this.convert(memberRows, input.getNames());
            memberRows = this.convert(memberRows, input.getGender());
            memberRows = this.convert(memberRows, input.getBirthDate());

        }

        return memberRows;
    }

    private MemRowList convert(MemRowList memberRows, LocalDate birthDate) {
        var memHead = this.getMemHead(memberRows);

        var memDate = new MemDate(memHead);
        memDate.setAttrCode("BIRTHDT");
        memDate.setDateVal(birthDate.toString());
        memberRows.addRow(memDate);
        return memberRows;

    }

}