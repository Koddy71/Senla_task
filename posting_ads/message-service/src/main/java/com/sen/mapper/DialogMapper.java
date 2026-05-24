package com.sen.mapper;

import org.mapstruct.Mapper;

import com.sen.dto.response.DialogResponse;
import com.sen.entity.Dialog;

@Mapper(componentModel = "spring")
public interface DialogMapper {

    DialogResponse toDialogResponse(Dialog dialog, String userLogin1, String userLogin2);

}
