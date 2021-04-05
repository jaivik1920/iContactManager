/**
 * 
 */

console.log("ihh");

const makePayment=()=>{
	console.log("hii");
	
	let amount=$("#payamount").val();
	if(amount=='' || amount==null){
		
		swal("Error", "Please enter amount!!!", "error");		
	}
	else{
		$.ajax(
			{
				url:"/iContactManager/user/createpayment",
				data:JSON.stringify({amount:amount}),
				dataType:"json",
				contentType:"application/json",
				type:"POST",
				success:function(response){
		
					if(response.status=='created'){
						
					var options = {
			    "key": "rzp_test_qzfcNpMEANAbrK", // Enter the Key ID generated from the Dashboard
			    "amount": response.amount, // Amount is in currency subunits. Default currency is INR. Hence, 50000 refers to 50000 paise
			    "currency": "INR",
			    "name": "iContactManager",
			    "description": "transaction",
			    "image": "https://icon2.cleanpng.com/20171221/bie/telephone-png-image-5a3c3be5e34e11.8657516115138969339311.jpg",
			    "order_id": response.id, //This is a sample Order ID. Pass the `id` obtained in the response of Step 1
			    "handler": function (response){
			        console.log(response.razorpay_payment_id);
			        console.log(response.razorpay_order_id);
			        console.log(response.razorpay_signature);
			swal("Good job!", "Payment Successful!!!", "success");					
			    },
			    "prefill": {
			        "name": "",
			        "email": "",
			        "contact": ""
			    },
			    "notes": {
			        "address": "iSmartContactManager"
			    },
			    "theme": {
			        "color": "#000000"
			    }
			};
			
		
			var rzp1 = new Razorpay(options);
			rzp1.on('payment.failed', function (response){
	        console.log(response.error.code);
	        console.log(response.error.description);
	        console.log(response.error.source);
	        console.log(response.error.step);
	        console.log(response.error.reason);
	        console.log(response.error.metadata.order_id);
	        console.log(response.error.metadata.payment_id);
			swal("Error", "Oops,Something went wrong! Payment failed!!!", "error");		
			
			});
			rzp1.open();
			
			$("#payamount").val("");	
			}
					

				},
				error:function(error){
					swal("Error", "Oops,Something went wrong! Payment failed!!!", "error");		
				}
			}
		)	
	}
}