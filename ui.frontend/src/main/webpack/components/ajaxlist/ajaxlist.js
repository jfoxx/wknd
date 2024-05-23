import jQuery from "jquery";

(function ($) {
    'use strict';
    $(document).ready( function () {
      if($('ul#ajaxlist').length) {

     $.getJSON( "https://prod-90.westus.logic.azure.com:443/workflows/3a7833ec393d4899960be828de5e1b56/triggers/manual/paths/invoke?api-version=2016-06-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=iGPmU7XD5q0oSwmENIeGoGV_SGWchkfxWIin7oVEmI8" )
      .done(function( data ) {
            console.log(data);
          $.each( data, function( i, element ) {
            var link = "<li><a href='"+ element.ServerRelativeUrl + "'><span class='gp-"+element.GreenPercentage+"'></span><span class='title'>"+element.Title+"</span><span class='servicetype'>"+element.ServiceType+"</span><span class='term'>"+element.Term+"</span><span class='price'>$"+ element.Price.toFixed(2) +"</span></a></li>"
            $('#ajaxlist').append(link);
          });
        });

      }


    });

    }(jQuery));